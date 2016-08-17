package org.wordpress.aztec.demo

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import org.wordpress.aztec.*

class MainActivity : Activity(), OnImeBackListener {

    companion object {
        private val BOLD = "<b>Bold</b><br><br>"
        private val ITALIC = "<i>Italic</i><br><br>"
        private val UNDERLINE = "<u>Underline</u><br><br>"
        private val STRIKETHROUGH = "<s class=\"test\">Strikethrough</s><br><br>" // <s> or <strike> or <del>
        private val BULLET = "<ul><li>asdfg</li></ul>"
        private val QUOTE = "<blockquote>Quote</blockquote>"
        private val LINK = "<a href=\"https://github.com/wordpress-mobile/WordPress-Aztec-Android\">Link</a><br><br>"
        private val UNKNOWN = "<iframe class=\"classic\">Menu</iframe><br><br>"
        private val COMMENT = "<!--This is a comment--><br><br>"
        private val HIDDEN = 
                "<span></span>" +
                "<div class=\"first\">" +
                "    <div class=\"second\">" +
                "        <div class=\"third\">" +
                "            Div<br><span><b>Span</b></span><br>Hidden" +
                "        </div>" +
                "        <div class=\"fourth\"></div>" +
                "        <div class=\"fifth\"></div>" +
                "    </div>" +
                "    <span class=\"second last\"></span>" +
                "</div>" +
                "<br><br>"
        private val EXAMPLE = BOLD + ITALIC + UNDERLINE + STRIKETHROUGH + BULLET + QUOTE + LINK + UNKNOWN + COMMENT + HIDDEN
    }

    private lateinit var aztec: AztecText
    private lateinit var source: SourceViewEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        aztec = findViewById(R.id.aztec) as AztecText
        source = findViewById(R.id.source) as SourceViewEditText

        source.setOnImeBackListener(this)
        source.addTextChangedListener(HtmlStyleTextWatcher());

        // ImageGetter coming soon...
        aztec.fromHtml(EXAMPLE)
        aztec.setSelection(aztec.editableText.length)

        setupBold()
        setupItalic()
        setupUnderline()
        setupStrikethrough()
        setupBullet()
        setupQuote()
        setupLink()
        setupClear()
        setupHtml()
    }

    override fun onImeBack() {

    }


    private fun setupBold() {
        val bold = findViewById(R.id.bold) as ImageButton

        bold.setOnClickListener { aztec.bold(!aztec.contains(AztecText.FORMAT_BOLD)) }

        bold.setOnLongClickListener {
            Toast.makeText(this@MainActivity, R.string.toast_bold, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupItalic() {
        val italic = findViewById(R.id.italic) as ImageButton

        italic.setOnClickListener { aztec.italic(!aztec.contains(AztecText.FORMAT_ITALIC)) }

        italic.setOnLongClickListener {
            Toast.makeText(this@MainActivity, R.string.toast_italic, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupUnderline() {
        val underline = findViewById(R.id.underline) as ImageButton

        underline.setOnClickListener { aztec.underline(!aztec.contains(AztecText.FORMAT_UNDERLINED)) }

        underline.setOnLongClickListener {
            Toast.makeText(this@MainActivity, R.string.toast_underline, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupStrikethrough() {
        val strikethrough = findViewById(R.id.strikethrough) as ImageButton

        strikethrough.setOnClickListener { aztec.strikethrough(!aztec.contains(AztecText.FORMAT_STRIKETHROUGH)) }

        strikethrough.setOnLongClickListener {
            Toast.makeText(this@MainActivity, R.string.toast_strikethrough, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupBullet() {
        val bullet = findViewById(R.id.bullet) as ImageButton

        bullet.setOnClickListener { aztec.bullet(!aztec.contains(AztecText.FORMAT_BULLET)) }


        bullet.setOnLongClickListener {
            Toast.makeText(this@MainActivity, R.string.toast_bullet, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupQuote() {
        val quote = findViewById(R.id.quote) as ImageButton

        quote.setOnClickListener { aztec.quote(!aztec.contains(AztecText.FORMAT_QUOTE)) }

        quote.setOnLongClickListener {
            Toast.makeText(this@MainActivity, R.string.toast_quote, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupLink() {
        val link = findViewById(R.id.link) as ImageButton

        link.setOnClickListener { showLinkDialog() }

        link.setOnLongClickListener {
            Toast.makeText(this@MainActivity, R.string.toast_insert_link, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupClear() {
        val clear = findViewById(R.id.clear) as ImageButton

        clear.setOnClickListener { aztec.clearFormats() }

        clear.setOnLongClickListener {
            Toast.makeText(this@MainActivity, R.string.toast_format_clear, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupHtml() {
        val html = findViewById(R.id.html) as Button

        html.setOnClickListener {

            val styledHtml = android.text.SpannableString(aztec.toHtml())
            HtmlStyleUtils.styleHtmlForDisplay(styledHtml)

            aztec.visibility = View.GONE;
            source.setText(styledHtml)
            source.visibility = View.VISIBLE
        }

        html.setOnLongClickListener {
            Toast.makeText(this@MainActivity, R.string.toast_html, Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun showLinkDialog() {
        val start = aztec.selectionStart
        val end = aztec.selectionEnd

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)

        val view = layoutInflater.inflate(R.layout.dialog_link, null, false)
        val editText = view.findViewById(R.id.edit) as EditText
        builder.setView(view)
        builder.setTitle(R.string.dialog_title)

        builder.setPositiveButton(R.string.dialog_button_ok, DialogInterface.OnClickListener { dialog, which ->
            val link = editText.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(link)) {
                return@OnClickListener
            }

            // When AztecText lose focus, use this method
            aztec.link(link, start, end)
        })
        builder.create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.undo -> aztec.undo()
            R.id.redo -> aztec.redo()
            else -> {
            }
        }

        return true
    }
}
