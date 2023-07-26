import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.internet.unetspeedmeter.CUSTOM_BROADCAST


abstract class CustomBroadcast : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {


        if(intent!!.action == CUSTOM_BROADCAST){
            onDataReceive(intent.getStringExtra("INTERNET_SEND")!!,intent.getStringExtra("INTERNET_RECEIVE")!!)
        }
    }

    abstract fun onDataReceive(speedSend:String,speedReceiver: String)

}