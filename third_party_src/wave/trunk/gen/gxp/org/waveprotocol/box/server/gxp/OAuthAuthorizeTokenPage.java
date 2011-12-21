// ===================================================================
//
//   WARNING: GENERATED CODE! DO NOT EDIT!
//
// ===================================================================
/*
 This file generated from:

 /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp
*/

package org.waveprotocol.box.server.gxp;

import com.google.gxp.base.*;
import com.google.gxp.css.*;                                                    // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L9, C79
import com.google.gxp.html.*;                                                   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L9, C79
import com.google.gxp.js.*;                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L9, C79
import com.google.gxp.text.*;                                                   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L9, C79

public class OAuthAuthorizeTokenPage extends com.google.gxp.base.GxpTemplate {   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L9, C79

  private static final String GXP$MESSAGE_SOURCE = "org.waveprotocol.box.server.gxp";

  public static void write(final java.lang.Appendable gxp$out, final com.google.gxp.base.GxpContext gxp_context, final int numberOfMinutes, final String xsrfToken)
      throws java.io.IOException {
    final java.util.Locale gxp_locale = gxp_context.getLocale();
    gxp$out.append("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"");   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L14, C3
    if (gxp_context.isUsingXmlSyntax()) {                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L17, C3
      gxp$out.append(" /");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L17, C3
    }
    gxp$out.append(">\n<title>Authorization Required</title>\n<link rel=\"shortcut icon\" href=\"/static/favicon.ico\"");   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L17, C3
    if (gxp_context.isUsingXmlSyntax()) {                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L19, C3
      gxp$out.append(" /");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L19, C3
    }
    gxp$out.append("></head>\n<body>A program would like to access your waves and perform operations on your\nbehalf. If you authorize the program it will have access to your data for\n");   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L19, C3
    com.google.gxp.html.HtmlAppender.INSTANCE.append(gxp$out, gxp_context, (numberOfMinutes));   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L25, C3
    gxp$out.append("\nminutes.\n<form method=\"post\" action=\"\"><input type=\"hidden\" name=\"token\"");   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L25, C38
    gxp$out.append(" value=\"");                                                // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L29, C3
    com.google.gxp.html.HtmlAppender.INSTANCE.append(gxp$out, gxp_context, (xsrfToken));   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L30, C5
    gxp$out.append("\"");                                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L29, C3
    if (gxp_context.isUsingXmlSyntax()) {                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L28, C33
      gxp$out.append(" /");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L28, C33
    }
    gxp$out.append(">\n<table><tr><input type=\"submit\" value=\"Agree\" name=\"agree\"");   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L28, C33
    if (gxp_context.isUsingXmlSyntax()) {                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L34, C7
      gxp$out.append(" /");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L34, C7
    }
    gxp$out.append(">\n<input type=\"submit\" value=\"Cancel\" name=\"cancel\"");   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L34, C7
    if (gxp_context.isUsingXmlSyntax()) {                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L35, C7
      gxp$out.append(" /");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L35, C7
    }
    gxp$out.append("></tr></table></form></body></html>");                      // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/OAuthAuthorizeTokenPage.gxp: L35, C7
  }

  private static final java.util.List<String> GXP$ARGLIST = java.util.Collections.unmodifiableList(java.util.Arrays.asList("numberOfMinutes", "xsrfToken"));

  /**
   * @return the names of the user defined arguments to this template.
   * This is sort of like a mapping between the positional and named
   * parameters. The first two parameters (common to all templates) are
   * not included in this list. (BTW: No, Java reflection does not
   * provide this information)
   */
  public static java.util.List<String> getArgList() {
    return GXP$ARGLIST;
  }

  private abstract static class TunnelingHtmlClosure
      extends GxpTemplate.TunnelingGxpClosure
      implements com.google.gxp.html.HtmlClosure {
  }

  public static com.google.gxp.html.HtmlClosure getGxpClosure(final int numberOfMinutes, final String xsrfToken) {
    return new TunnelingHtmlClosure() {
      public void writeImpl(final java.lang.Appendable gxp$out, final com.google.gxp.base.GxpContext gxp_context)
          throws java.io.IOException {
        org.waveprotocol.box.server.gxp.OAuthAuthorizeTokenPage.write(gxp$out, gxp_context, numberOfMinutes, xsrfToken);
      }
    };
  }

  /**
   * Interface that defines a strategy for writing this GXP
   */
  public interface Interface {
    public void write(final java.lang.Appendable gxp$out, final com.google.gxp.base.GxpContext gxp_context, final int numberOfMinutes, final String xsrfToken)
        throws java.io.IOException;

    public com.google.gxp.html.HtmlClosure getGxpClosure(final int numberOfMinutes, final String xsrfToken);
  }

  /**
   * Instantiable instance of this GXP
   */
  public static class Instance implements Interface {

    public Instance() {
    }

    public void write(final java.lang.Appendable gxp$out, final com.google.gxp.base.GxpContext gxp_context, final int numberOfMinutes, final String xsrfToken)
        throws java.io.IOException {
      org.waveprotocol.box.server.gxp.OAuthAuthorizeTokenPage.write(gxp$out, gxp_context, numberOfMinutes, xsrfToken);
    }

    public com.google.gxp.html.HtmlClosure getGxpClosure(final int numberOfMinutes, final String xsrfToken) {
      return new TunnelingHtmlClosure() {
        public void writeImpl(final java.lang.Appendable gxp$out, final com.google.gxp.base.GxpContext gxp_context)
            throws java.io.IOException {
          Instance.this.write(gxp$out, gxp_context, numberOfMinutes, xsrfToken);
        }
      };
    }
  }
}

// ===================================================================
//
//   WARNING: GENERATED CODE! DO NOT EDIT!
//
// ===================================================================
