package com.myapp.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HtmlUtilTest {

    @Test
    void escape_handlesNullAndEmpty() {
        assertEquals("", HtmlUtil.escape(null));
        assertEquals("", HtmlUtil.escape(""));
    }

    @Test
    void escape_encodesHtmlSensitiveCharacters() {
        String raw = "<script>alert(\"x\") & '/</script>";
        String escaped = HtmlUtil.escape(raw);

        assertEquals("&lt;script&gt;alert(&quot;x&quot;) &amp; &#x27;&#x2F;&lt;&#x2F;script&gt;", escaped);
    }

    @Test
    void escapeJsString_escapesControlAndDangerousCharacters() {
        String raw = "line1\n\"quoted\" 'single' <tag> & \\\\";
        String escaped = HtmlUtil.escapeJsString(raw);

        assertEquals("line1\\n\\\"quoted\\\" \\'single\\' \\u003Ctag\\u003E \\u0026 \\\\\\\\", escaped);
    }
}
