package br.edu.ifsp.scl.sdm.currencyconverter.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import br.edu.ifsp.scl.sdm.currencyconverter.R
import br.edu.ifsp.scl.sdm.currencyconverter.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.currencyconverter.model.livedata.CurrencyConverterLiveData
import br.edu.ifsp.scl.sdm.currencyconverter.service.ConvertService
import br.edu.ifsp.scl.sdm.currencyconverter.ui.viewmodel.CurrencyConverterViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.properties.ReadOnlyProperty


class MainActivity : AppCompatActivity() {
    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val ccvm: CurrencyConverterViewModel by ViewModels()

    private fun ViewModels(): ReadOnlyProperty<MainActivity, CurrencyConverterViewModel> {
        return object : ReadOnlyProperty<MainActivity, CurrencyConverterViewModel> {
            override fun getValue(thisRef: MainActivity, property: kotlin.reflect.KProperty<*>): CurrencyConverterViewModel {
                return ViewModelProvider(thisRef).get(CurrencyConverterViewModel::class.java)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        setSupportActionBar(amb.mainTb.apply { title = getString(R.string.app_name) })

        var fromQuote = ""
        var toQuote = ""
        val currenciesAdapter =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        with(amb) {
            fromQuoteMactv.apply {
                setAdapter(currenciesAdapter)
                setOnItemClickListener { _, _, _, _ -> fromQuote = text.toString() }

            }

            toQuoteMactv.apply {
                setAdapter(currenciesAdapter)
                setOnItemClickListener { _, _, _, _ -> toQuote= text.toString() }
            }
            convertBt.setOnClickListener {
                ccvm.convert(fromQuote, toQuote, amountTiet.text.toString())
            }
        }
        CurrencyConverterLiveData.currenciesLiveData.observe(this) { currencyList ->
            currenciesAdapter.clear()
            currenciesAdapter.addAll(currencyList.currencies.keys.sorted())
            currenciesAdapter.getItem(0)?.also { quote ->
                amb.fromQuoteMactv.setText(quote, false)
                fromQuote = quote
            }
            currenciesAdapter.getItem(currenciesAdapter.count - 1)?.also { quote ->
                amb.toQuoteMactv.setText(quote, false)
                toQuote = quote
            }
        }

        CurrencyConverterLiveData.conversionResultLiveData.observe(this) { conversionResult ->
            with(amb) {
                conversionResult.rates.values.first().rateForAmount.also {
                    resultTiet.setText(it)
                }
            }
        }

        ccvm.getCurrencies()
    }


}
