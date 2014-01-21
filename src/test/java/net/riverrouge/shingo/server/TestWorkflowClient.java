package net.riverrouge.shingo.server;

import static net.riverrouge.shingo.server.TestConstants.*;
import static org.junit.Assert.*;

import net.riverrouge.shingo.server.api.Facade;
import net.riverrouge.shingo.server.db.Datastore;
import net.riverrouge.shingo.server.model.Execution;
import net.riverrouge.shingo.server.model.Memo;
import net.riverrouge.shingo.server.model.WorkflowType;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * An integration test that tests a number of workflow steps consecutively
 */
public class TestWorkflowClient {

  private static String dir;

  private static final String VCF_BUCKET = "";

  static {
    dir = System.getProperty("user.dir") + "/src/main/webapp/WEB-INF/queue.xml";
  }

  private final ScheduledExecutorService annotationScheduler =
      Executors.newSingleThreadScheduledExecutor();

  private final ScheduledExecutorService uploadScheduler =
      Executors.newSingleThreadScheduledExecutor();

  private final ScheduledExecutorService deciderScheduler =
      Executors.newSingleThreadScheduledExecutor();

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig(),
          new LocalTaskQueueTestConfig().setQueueXmlPath(dir));

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testRun() throws IOException {
    String workflowDescription = "This workflow performs functional annotations of single-sample " +
        "human variant call files";

    Facade.registerWorkflowType(
        WORKFLOW_TYPE_NAME,
        WORKFLOW_TYPE_VERSION,
        workflowDescription,
        3600);

    startup();

    WorkflowType workflowType = Datastore.fetchWorkflowType(WORKFLOW_TYPE_NAME,
        WORKFLOW_TYPE_VERSION);
    assertTrue(workflowType != null);   // Now we know the WFType is registered correctly

    // Create a new Memo with data about where to find the vcf file to process
    Memo memo = new Memo();
    String noteKey = "input vcf file";
    String noteValue = "blah/blah/input_file_location_.vcf";
    memo.putNote(noteKey, noteValue);

    // Start an execution of the new workflow type, passing in the identifying name and the memo
    Facade.startWorkflow(EXECUTION_NAME, WORKFLOW_TYPE_NAME, WORKFLOW_TYPE_VERSION,
        INITIATE_EXECUTION_DECISION, memo);

    Execution execution = Datastore.fetchExecution(EXECUTION_NAME);
    assertNotNull(execution);
    assertEquals(noteValue, execution.getMemo().getNote(noteKey));

    System.out.println(execution.toString());
  }

  /**
   * Activates all the workers
   */
  public void activateWorkers() {
    TestAnnotationWorker annotationWorker = new TestAnnotationWorker();
    annotationScheduler.scheduleWithFixedDelay(annotationWorker, 0, 1, TimeUnit.SECONDS);
    TestUploadWorker uploadWorker = new TestUploadWorker();
    uploadScheduler.scheduleWithFixedDelay(uploadWorker, 0, 1, TimeUnit.SECONDS);
  }

  /**
   * Activates the decider
   */
  public void activateDecider() {
    TestDecider decider = new TestDecider(WORKFLOW_TYPE_NAME, WORKFLOW_TYPE_VERSION);
    deciderScheduler.scheduleWithFixedDelay(decider, 0, 1, TimeUnit.SECONDS);
  }

  /**
   * Deactivates all the workers
   */
  private void deactivateWorkers() {
    annotationScheduler.shutdown();
    uploadScheduler.shutdown();
  }

  public void shutdown() {
    deactivateDecider();
    deactivateWorkers();
  }

  private void deactivateDecider() {
    deciderScheduler.shutdown();
  }

  private void startup() throws IOException {
    activateWorkers();
    activateDecider();
    Path vcfDir = Paths.get(VCF_BUCKET);
    new NewDataWorker(vcfDir);
  }
}
