import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.mqtt._
import org.eclipse.paho.client.mqttv3._

 class Customer (val customerID:Int, val TimeStamp:Int, val Period:Int, val Channel:Int) extends java.io.Serializable {
 }
 object SimpleApp {
  def main(args: Array[String]) {
    val sparkConf = new SparkConf().setAppName("SimpleApp").setMaster("local[2]")
    // Create the context with a 1 second batch size
    val ssc = new StreamingContext(sparkConf, Seconds(5))
    ssc.checkpoint(".")

    val lines = MQTTUtils.createStream(ssc,"tcp://127.0.0.1:2099","TV",StorageLevel.MEMORY_ONLY_SER_2)
    val words = lines.map(row=>{
      val row_val= row.split(",")
      new Customer(row_val(0).toInt, row_val(1).toInt, row_val(2).toInt, row_val(3).toInt)
      })
    val wordDstream = words.map(x => (x.customerID.toString+x.TimeStamp.toString, x))
    wordDstream.print()

    // Update the cumulative count using mapWithState
    // This will give a DStream made of state (which is the cumulative count of the words)
    def analyzeCustomer(CustomerId: String,
                                                data: Option[Customer],
                                                state:State[Customer]): Option[Customer] = {
      data match {
        case Some(data) => state.update(data)
        case None=> None
      }
        data
    }

    val spec = StateSpec.function(analyzeCustomer _ )
    val updatedDStream = wordDstream.mapWithState(spec)
    updatedDStream.print()
    val stateSnapshotStream = updatedDStream.stateSnapshots() 
    stateSnapshotStream.print() 
    ssc.start()
    ssc.awaitTermination()
}
}
// scalastyle:on println
