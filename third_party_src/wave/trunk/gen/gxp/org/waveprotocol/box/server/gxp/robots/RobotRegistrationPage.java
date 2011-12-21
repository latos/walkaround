// ===================================================================
//
//   WARNING: GENERATED CODE! DO NOT EDIT!
//
// ===================================================================
/*
 This file generated from:

 /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp
*/

package org.waveprotocol.box.server.gxp.robots;

import com.google.gxp.base.*;
import com.google.gxp.css.*;                                                    // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L10, C44
import com.google.gxp.html.*;                                                   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L10, C44
import com.google.gxp.js.*;                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L10, C44
import com.google.gxp.text.*;                                                   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L10, C44

public class RobotRegistrationPage extends com.google.gxp.base.GxpTemplate {    // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L10, C44

  private static final String GXP$MESSAGE_SOURCE = "org.waveprotocol.box.server.gxp";

  public static void write(final java.lang.Appendable gxp$out, final com.google.gxp.base.GxpContext gxp_context, final String domain, final String message)
      throws java.io.IOException {
    final java.util.Locale gxp_locale = gxp_context.getLocale();
    gxp$out.append("<html><head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"");   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L15, C3
    if (gxp_context.isUsingXmlSyntax()) {                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L17, C7
      gxp$out.append(" /");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L17, C7
    }
    gxp$out.append(">\n<title>Robot Registration</title>\n<link rel=\"shortcut icon\" href=\"/static/favicon.ico\"");   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L17, C7
    if (gxp_context.isUsingXmlSyntax()) {                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L19, C7
      gxp$out.append(" /");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L19, C7
    }
    gxp$out.append("></head>\n<body>");                                         // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L19, C7
    if (!message.isEmpty()) {                                                   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L24, C7
      gxp$out.append("<b>");                                                    // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L25, C9
      com.google.gxp.html.HtmlAppender.INSTANCE.append(gxp$out, gxp_context, (message));   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L25, C12
      gxp$out.append("</b>");                                                   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L25, C9
    }
    gxp$out.append("\n<form method=\"post\" action=\"\">Robot Username: <input name=\"username\" type=\"text\"");   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L26, C16
    if (gxp_context.isUsingXmlSyntax()) {                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L29, C25
      gxp$out.append(" /");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L29, C25
    }
    gxp$out.append(">@");                                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L29, C25
    com.google.gxp.html.HtmlAppender.INSTANCE.append(gxp$out, gxp_context, (domain));   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L29, C62
    gxp$out.append("<br");                                                      // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L29, C87
    if (gxp_context.isUsingXmlSyntax()) {                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L29, C87
      gxp$out.append(" /");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L29, C87
    }
    gxp$out.append(">\nRobot URL: <input name=\"location\" type=\"text\"");     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L29, C87
    if (gxp_context.isUsingXmlSyntax()) {                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L30, C20
      gxp$out.append(" /");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L30, C20
    }
    gxp$out.append("><br");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L30, C20
    if (gxp_context.isUsingXmlSyntax()) {                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L30, C56
      gxp$out.append(" /");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L30, C56
    }
    gxp$out.append(">\n<input type=\"submit\"");                                // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L30, C56
    if (gxp_context.isUsingXmlSyntax()) {                                       // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L31, C9
      gxp$out.append(" /");                                                     // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L31, C9
    }
    gxp$out.append("></form></body></html>");                                   // /home/dan/walkaround/third_party_src/wave/trunk/src/org/waveprotocol/box/server/gxp/robots/RobotRegistrationPage.gxp: L31, C9
  }

  private static final java.util.List<String> GXP$ARGLIST = java.util.Collections.unmodifiableList(java.util.Arrays.asList("domain", "message"));

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

  public static com.google.gxp.html.HtmlClosure getGxpClosure(final String domain, final String message) {
    return new TunnelingHtmlClosure() {
      public void writeImpl(final java.lang.Appendable gxp$out, final com.google.gxp.base.GxpContext gxp_context)
          throws java.io.IOException {
        org.waveprotocol.box.server.gxp.robots.RobotRegistrationPage.write(gxp$out, gxp_context, domain, message);
      }
    };
  }

  /**
   * Interface that defines a strategy for writing this GXP
   */
  public interface Interface {
    public void write(final java.lang.Appendable gxp$out, final com.google.gxp.base.GxpContext gxp_context, final String domain, final String message)
        throws java.io.IOException;

    public com.google.gxp.html.HtmlClosure getGxpClosure(final String domain, final String message);
  }

  /**
   * Instantiable instance of this GXP
   */
  public static class Instance implements Interface {

    public Instance() {
    }

    public void write(final java.lang.Appendable gxp$out, final com.google.gxp.base.GxpContext gxp_context, final String domain, final String message)
        throws java.io.IOException {
      org.waveprotocol.box.server.gxp.robots.RobotRegistrationPage.write(gxp$out, gxp_context, domain, message);
    }

    public com.google.gxp.html.HtmlClosure getGxpClosure(final String domain, final String message) {
      return new TunnelingHtmlClosure() {
        public void writeImpl(final java.lang.Appendable gxp$out, final com.google.gxp.base.GxpContext gxp_context)
            throws java.io.IOException {
          Instance.this.write(gxp$out, gxp_context, domain, message);
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
