package $package$.support

import java.util.UUID

import grizzled.slf4j.Logging
import org.quartz._

import scala.collection.JavaConversions._

class InnerJob extends Job {
  override def execute(context: JobExecutionContext) = context.getJobDetail.getJobDataMap.get("instance").asInstanceOf[JobWrapper].run()
}

trait JobWrapper {
  def run(): Unit
}

trait SchedulerSupport extends Logging {
  val schedFact = new org.quartz.impl.StdSchedulerFactory()
  val sched = schedFact.getScheduler()
  sched.start()

  def startJob(cron: String, f: => Unit) = {
    logger.info(s"start job, cron: \$cron")
    val job = JobBuilder.newJob(classOf[InnerJob])
      .usingJobData(new JobDataMap(Map("instance" -> new JobWrapper {
        override def run() = f
      })))
      .withIdentity(UUID.randomUUID().toString)
      .build()
    // Trigger the job to run now, and then every 40 seconds
    val trigger = TriggerBuilder.newTrigger()
      .withIdentity(UUID.randomUUID().toString)
      .startNow()
      .withSchedule(CronScheduleBuilder.cronSchedule(cron))
      .build()
    // Tell quartz to schedule the job using our trigger
    sched.scheduleJob(job, trigger)
  }
}
