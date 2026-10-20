package am.currency.widget

import android.app.Activity
import android.os.Bundle
import android.graphics.Color
import android.view.ViewGroup
import android.widget.*
import java.util.Locale

class MainActivity:Activity() {
 override fun onCreate(savedInstanceState:Bundle?) { super.onCreate(savedInstanceState)
  val p=getSharedPreferences("settings",MODE_PRIVATE)
  val root=LinearLayout(this).apply { orientation=LinearLayout.VERTICAL;setPadding(32,45,32,20);setBackgroundColor(Color.WHITE) }
  fun label(s:String)=TextView(this).apply { text=s;textSize=17f;setTextColor(Color.DKGRAY);setPadding(0,15,0,5) }
  root.addView(label("Currency Widget · Official CBA reference rates"))
  val amount=EditText(this).apply { inputType=android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;setSingleLine(true);setText(p.getString("amount","10000"));hint="Amount" }
  root.addView(label("Amount"));root.addView(amount)
  val codes=listOf("KZT","AMD","USD","EUR","GBP","RUB","CNY","GEL","JPY","CHF","CAD","AUD","AED","INR","TRY","UZS","KGS","BYN","SEK","NOK","PLN","CZK","SGD")
  fun selector(title:String,key:String,default:String):Spinner { root.addView(label(title));val s=Spinner(this);s.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,codes);s.setSelection(codes.indexOf(p.getString(key,default)).coerceAtLeast(0));root.addView(s);return s }
  val base=selector("From currency","base","KZT");val one=selector("First output","one","AMD");val two=selector("Second output","two","USD")
  val status=label("Cached rates: ${p.getString("date","none")}");root.addView(status)
  val save=Button(this).apply { text="Save and update widget" };root.addView(save)
  save.setOnClickListener { p.edit().putString("amount",amount.text.toString()).putString("base",codes[base.selectedItemPosition]).putString("one",codes[one.selectedItemPosition]).putString("two",codes[two.selectedItemPosition]).apply();CurrencyWidget.render(this);finish() }
  val refresh=Button(this).apply { text="Refresh official rates" };root.addView(refresh)
  refresh.setOnClickListener { refresh.isEnabled=false;status.text="Updating…";Thread { val r=RateService.refresh(this);runOnUiThread { refresh.isEnabled=true;status.text=r.fold({"Updated $it currencies. Save to finish."},{"Update failed: ${it.message}. Cached rates retained."}) } }.start() }
  setContentView(ScrollView(this).apply { addView(root) })
  if(RateService.cached(this).isEmpty()) Thread { RateService.refresh(this) }.start()
 }
}
