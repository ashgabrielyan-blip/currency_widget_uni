package am.currency.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.text.NumberFormat
import java.util.Locale

class CurrencyWidget : AppWidgetProvider() {
 override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) { render(context) }
 companion object {
  fun render(c: Context) {
   val p=c.getSharedPreferences("settings",Context.MODE_PRIVATE)
   val codes=listOf(p.getString("base","KZT")!!,p.getString("one","AMD")!!,p.getString("two","USD")!!)
   val amount=p.getString("amount","10000")!!.toDoubleOrNull() ?: 0.0
   val rates=RateService.cached(c)
   val nf=NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits=2 }
   fun converted(code:String):String { val from=if(codes[0]=="AMD") 1.0 else rates[codes[0]]; val to=if(code=="AMD") 1.0 else rates[code]; return if(from==null||to==null||from<=0) "$code: rates unavailable" else "$code: ${nf.format(amount*from/to)}" }
   val rv=RemoteViews(c.packageName,R.layout.widget)
   rv.setTextViewText(R.id.base,"${nf.format(amount)} ${codes[0]}")
   rv.setTextViewText(R.id.first,converted(codes[1]));rv.setTextViewText(R.id.second,converted(codes[2]))
   rv.setTextViewText(R.id.status,"CBA reference rates · ${p.getString("date","not updated")}")
   val pi=PendingIntent.getActivity(c,0,Intent(c,MainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
   for(id in listOf(R.id.title,R.id.base,R.id.first,R.id.second,R.id.status)) rv.setOnClickPendingIntent(id,pi)
   AppWidgetManager.getInstance(c).updateAppWidget(ComponentName(c,CurrencyWidget::class.java),rv)
  }
 }
}
