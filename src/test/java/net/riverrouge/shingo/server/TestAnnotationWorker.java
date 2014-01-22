package net.riverrouge.shingo.server;

import static net.riverrouge.shingo.server.TestConstants.*;

import net.riverrouge.shingo.server.api.Facade;
import net.riverrouge.shingo.server.api.GenericResponse;
import net.riverrouge.shingo.server.model.Task;

import java.util.logging.Logger;

/**
 * A fake annotation worker
 */
public class TestAnnotationWorker implements Runnable {

  private static final Logger LOG = Logger.getLogger(TestAnnotationWorker.class.getName());

  void handleWork() {

    GenericResponse response =
        Facade.getTask(WORKFLOW_TYPE_NAME,  WORKFLOW_TYPE_VERSION, ANNOTATION_TASK_TAG);

    Task task = response.getTask();
    if (task != null) {

      // For demonstration purposes, get the location of the file to annotate"
      String vcfFileString = task.getExecution().getMemo().getNote("input vcf file");
      if (vcfFileString == null) {
        throw new RuntimeException("Can't find the fucking note.");
      }

    /*
     * Lets assume for testing that we did the annotation. Update the memo with the location of the
     * annotated file.
     */
      task.getExecution().getMemo().putNote("output vcf file", "output_file_name.vcf");
      Facade.completeTask(task.getId());
    } else {
      LOG.info("No task found for annotation.");
    }
  }

  /**
   * When an object implementing interface <code>Runnable</code> is used to create a thread,
   * starting the thread causes the object's <code>run</code> method to be called in that separately
   * executing thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    handleWork();
  }
}
