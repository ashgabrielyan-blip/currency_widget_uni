package am.currency.widget

import android.content.Context
import org.w3c.dom.Element
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory

object RateService {
 fun cached(c:Context):Map<String,Double> { val p=c.getSharedPreferences("rates",Context.MODE_PRIVATE);return p.all.mapNotNull { (k,v)->(v as? Float)?.let { k to it.toDouble() } }.toMap() }
 fun refresh(c:Context):Result<Int> = runCatching {
  val conn=URL("https://api.cba.am/exchangerates.asmx").openConnection() as HttpURLConnection
  conn.requestMethod="POST";conn.connectTimeout=12000;conn.readTimeout=12000;conn.doOutput=true
  conn.setRequestProperty("Content-Type","text/xml; charset=utf-8")
  conn.setRequestProperty("SOAPAction","http://www.cba.am/ExchangeRatesLatest")
  val body="""<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"><soap:Body><ExchangeRatesLatest xmlns="http://www.cba.am/"/></soap:Body></soap:Envelope>"""
  conn.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
  if(conn.responseCode!=200) error("CBA HTTP ${conn.responseCode}")
  val factory=DocumentBuilderFactory.newInstance().apply { setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);setFeature("http://xml.org/sax/features/external-general-entities",false);setFeature("http://xml.org/sax/features/external-parameter-entities",false) }
  val doc=conn.inputStream.use { factory.newDocumentBuilder().parse(it) };conn.disconnect()
  val nodes=doc.getElementsByTagName("ExchangeRate")
  val editor=c.getSharedPreferences("rates",Context.MODE_PRIVATE).edit();var count=0
  for(i in 0 until nodes.length) { val el=nodes.item(i) as Element
   fun field(s:String)=el.getElementsByTagName(s).item(0)?.textContent?.trim().orEmpty()
   val iso=field("ISO");val unit=field("Amount").toDoubleOrNull();val rate=field("Rate").toDoubleOrNull()
   if(iso.length==3 && unit!=null && unit>0 && rate!=null && rate>0) { editor.putFloat(iso,(rate/unit).toFloat());count++ }
  }
  if(count<3) error("No usable official rates returned")
  editor.apply()
  c.getSharedPreferences("settings",Context.MODE_PRIVATE).edit().putString("date",SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.US).format(Date())).apply()
  CurrencyWidget.render(c);count
 }
}
