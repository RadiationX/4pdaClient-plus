package org.softeg.slartus.forpdaplus.classes;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import org.softeg.slartus.forpdacommon.FileUtils;
import org.softeg.slartus.forpdaplus.Client;
import org.softeg.slartus.forpdaplus.R;
import org.softeg.slartus.forpdaplus.classes.forum.ExtTopic;
import org.softeg.slartus.forpdaplus.common.HtmlUtils;
import org.softeg.slartus.forpdaplus.emotic.Smiles;
import org.softeg.slartus.forpdaplus.prefs.HtmlPreferences;
import org.softeg.slartus.forpdaplus.prefs.Preferences;

import java.util.Hashtable;

/**
 * User: slinkin
 * Date: 26.03.12
 * Time: 16:50
 */
public class TopicBodyBuilder extends HtmlBuilder {

    public static final String NICK_SNAPBACK_TEMPLATE = "[SNAPBACK]%s[/SNAPBACK] [B]%s,[/B] \n";
    private Boolean m_Logined, m_IsWebviewAllowJavascriptInterface;
    private ExtTopic m_Topic;
    private String m_UrlParams;
    private HtmlPreferences m_HtmlPreferences;
    private Hashtable<String, String> m_EmoticsDict;
    private boolean m_MMod = false;
    private Boolean m_IsLoadImages = true;
    private Boolean m_IsShowAvatars = true;

    public TopicBodyBuilder(Context context, Boolean logined, ExtTopic topic, String urlParams,
                            Boolean isWebviewAllowJavascriptInterface) {

        m_HtmlPreferences = new HtmlPreferences();
        m_HtmlPreferences.load(context);
        m_EmoticsDict = Smiles.getSmilesDict();
        m_PostTemplate = FileUtils.readTrimRawTextFile(context, R.raw.post_header);
        m_IsWebviewAllowJavascriptInterface = isWebviewAllowJavascriptInterface;
        m_Logined = logined;
        m_UrlParams = urlParams;
        m_Topic = topic;
        m_IsLoadImages = WebViewExternals.isLoadImages("theme");
        m_IsShowAvatars = Preferences.Topic.isShowAvatars();
    }

    public void beginTopic() {
        String desc = TextUtils.isEmpty(m_Topic.getDescription()) ? "" : (", " + m_Topic.getDescription());
        super.beginHtml(m_Topic.getTitle() + desc);
        super.beginBody();
        m_Body.append("<div style=\"margin-top:").append(ACTIONBAR_TOP_MARGIN).append("\"/>\n");
        if (m_Topic.getPagesCount() > 1) {
            addButtons(m_Body, m_Topic.getCurrentPage(), m_Topic.getPagesCount(),
                    m_IsWebviewAllowJavascriptInterface, false, true);
        }

        m_Body.append(getTitleBlock());
    }

    public void endTopic() {
        m_Body.append("<div name=\"entryEnd\" id=\"entryEnd\"></div>\n");
        m_Body.append("<br/><br/>");
        if (m_Topic.getPagesCount() > 1) {
            addButtons(m_Body, m_Topic.getCurrentPage(), m_Topic.getPagesCount(),
                    m_IsWebviewAllowJavascriptInterface, false, false);
        }

        m_Body.append("<br/><br/>");
//        addPostForm(m_Body);

        m_Body.append("<div id=\"viewers\"><a ").append(getHtmlout(m_IsWebviewAllowJavascriptInterface, "showReadingUsers"))
                .append(" class=\"href_button\">Кто читает тему..</a></div><br/>\n");
        m_Body.append("<div id=\"writers\"><a ").append(getHtmlout(m_IsWebviewAllowJavascriptInterface, "showWriters"))
                .append(" class=\"href_button\">Кто писал сообщения..</a></div><br/><br/>\n");
        m_Body.append(getTitleBlock());


        m_Body.append("<div style=\"margin-top:80px\"/>\n");
        super.endBody();
        super.endHtml();
    }

    private String getSpoiler(String title, String body, Boolean opened) {
        return
                (
                        m_HtmlPreferences.isSpoilerByButton() ?
                                "<div class='hidetop' style='cursor:pointer;' >".concat(title).concat("</div>" +
                                        "<input class='spoiler_button' type=\"button\" value=\"+\" onclick=\"toggleSpoilerVisibility(this)\"/>")
                                :
                                "<div class='hidetop' style='cursor:pointer;' onclick=\"var _n=this.parentNode.getElementsByTagName('div')[1];" +
                                        "if(_n.style.display=='none'){_n.style.display='';}else{_n.style.display='none';}\">"
                                                .concat(title)
                ).concat("</div><div class='hidemain'").concat(opened?" ":" style=\"display:none\"").concat(">").concat(body).concat("</div>");
    }

    public void addPost(Post post, Boolean spoil) {

        m_Body.append("<div name=\"entry").append(post.getId()).append("\" id=\"entry").append(post.getId()).append("\"></div>\n");

        addPostHeader(m_Body, post);

        m_Body.append("<div id=\"msg").append(post.getId()).append("\" name=\"msg").append(post.getId()).append("\">");

        String postBody = post.getBody().trim();
        if (m_HtmlPreferences.isSpoilerByButton())
            postBody = HtmlPreferences.modifySpoiler(postBody);

        if (spoil)
            m_Body.append(getSpoiler("<b>(&gt;&gt;&gt;ШАПКА ТЕМЫ&lt;&lt;&lt;)</b>", postBody,false));
        else
            m_Body.append(postBody);
        m_Body.append("</div>\n\n");
        //m_Body.append("<div class=\"s_post_footer\"><table width=\"100%%\"><tr><td id=\""+post.getId()+"\"></td></tr></table></div>\n\n");

        addFooter(m_Body, post);
        m_Body.append("<div class=\"between_messages\"></div>");
    }

    public String getBody() {
        String res;
        if (m_HtmlPreferences.isUseLocalEmoticons()) {
            res = HtmlPreferences.modifyStyleImagesBody(m_Body.toString());
            res = HtmlPreferences.modifyEmoticons(res, m_EmoticsDict, true);
        } else {
            res = HtmlPreferences.modifyEmoticons(m_Body.toString(), m_EmoticsDict, false);
        }
        if (!m_IsLoadImages)
            res = HtmlPreferences.modifyAttachedImagesBody(m_IsWebviewAllowJavascriptInterface, res);
        return res;
    }

    public void addBody(String value) {
        m_Body.append(value);
    }

    public void addPoll(String value, boolean openSpoil) {
        m_Body.append("<div class=\"poll\">").append(getSpoiler("<b>Опрос</b>", value,openSpoil)).append("</div>");
    }

    public void clear() {
        m_Topic = null;
        m_Body = null;
    }

    private String getTitleBlock() {
        String desc = TextUtils.isEmpty(m_Topic.getDescription()) ? "" : (", " + m_Topic.getDescription());
        return "<div class=\"topic_title_post\"><a href=\"http://4pda.ru/forum/index.php?showtopic=" + m_Topic.getId() + (TextUtils.isEmpty(m_UrlParams) ? "" : ("&" + m_UrlParams)) + "\">" + m_Topic.getTitle() + desc + "</a></div>\n";
    }

    public static void addButtons(StringBuilder sb, int currentPage, int pagesCount, Boolean isUseJs,
                                  Boolean useSelectTextAsNumbers, Boolean top) {
        Boolean prevDisabled = currentPage == 1;
        Boolean nextDisabled = currentPage == pagesCount;
        sb.append("<div class=\"navi\" id=\"").append(top ? "top_navi" : "bottom_navi").append("\">\n");
        sb.append("<div class=\"first\"><a ").append(prevDisabled ? "#" : getHtmlout(isUseJs, "firstPage")).append(" class=\"href_button").append(prevDisabled ? "_disable" : "").append("\">&lt;&lt;</a></div>\n");
        sb.append("<div class=\"prev\"><a ").append(prevDisabled ? "#" : getHtmlout(isUseJs, "prevPage")).append(" class=\"href_button").append(prevDisabled ? "_disable" : "").append("\">  &lt;  </a></div>\n");
        String selectText = useSelectTextAsNumbers ? (currentPage + "/" + pagesCount) : "Выбор";
        sb.append("<div class=\"page\"><a ").append(getHtmlout(isUseJs, "jumpToPage")).append(" class=\"href_button\">").append(selectText).append("</a></div>\n");
        sb.append("<div class=\"next\"><a ").append(nextDisabled ? "#" : getHtmlout(isUseJs, "nextPage")).append(" class=\"href_button").append(nextDisabled ? "_disable" : "").append("\">  &gt;  </a></div>\n");
        sb.append("<div class=\"last\"><a ").append(nextDisabled ? "#" : getHtmlout(isUseJs, "lastPage")).append(" class=\"href_button").append(nextDisabled ? "_disable" : "").append("\">&gt;&gt;</a></div>\n");
        sb.append("</div>\n");

    }

    private String normParam(String paramName) {
        return HtmlUtils.modifyHtmlQuote(paramName).replace("'", "\\'").replace("\"", "&quot;");
    }

    public static String getHtmlout(Boolean webViewAllowJs, String methodName, String val1, String val2) {
        return getHtmlout(webViewAllowJs, methodName, new String[]{val1, val2});
    }

    private static String getHtmlout(Boolean webViewAllowJs, String methodName, String val1) {
        return getHtmlout(webViewAllowJs, methodName, new String[]{val1});
    }

    public static String getHtmlout(Boolean webViewAllowJs, String methodName) {
        return getHtmlout(webViewAllowJs, methodName, new String[0]);
    }


    public static String getHtmlout(Boolean webViewAllowJs, String methodName, String[] paramValues) {
        return getHtmlout(webViewAllowJs, methodName, paramValues, true);
    }

    public static String getHtmlout(Boolean webViewAllowJs, String methodName, String[] paramValues, Boolean modifyParams) {
        StringBuilder sb = new StringBuilder();
        if (!webViewAllowJs) {
            sb.append("href=\"http://www.HTMLOUT.ru/");
            sb.append(methodName).append("?");
            int i = 0;

            for (String paramName : paramValues) {
                sb.append("val").append(i).append("=").append(modifyParams ? Uri.encode(paramName) : paramName).append("&");
                i++;
            }

            sb = sb.delete(sb.length() - 1, sb.length());
            sb.append("\"");
        } else {

            sb.append(" onclick=\"window.HTMLOUT.").append(methodName).append("(");
            for (String paramName : paramValues) {
                sb.append("'").append(paramName).append("',");
            }
            if (paramValues.length > 0)
                sb.delete(sb.length() - 1, sb.length());
            sb.append(")\"");
        }
        return sb.toString();
    }

    private String m_PostTemplate = null;

    private void addPostHeader(StringBuilder sb, Post msg) {
        String nick = msg.getNick();
        String nickParam = msg.getNickParam();
        String[] repParams = new String[]{msg.getId(), msg.getUserId(), nickParam, msg.getCanPlusRep() ? "1" : "0", msg.getCanMinusRep() ? "1" : "0"};


        String nickLink = nick;
        if (!TextUtils.isEmpty(msg.getUserId())) {
            nickLink = "<a " +
                    getHtmlout(m_IsWebviewAllowJavascriptInterface,
                            "showUserMenu", new String[]{
                                    msg.getId(),
                                    msg.getUserId(),
                                    nickParam, msg.getAvatarFileName()}
                    )
                    + " class=\"system_link\">" + nick + "</a>";
        }


        String userState = msg.getUserState() ? "post_nick_online_cli" : "post_nick_cli";

        String advDiv = m_IsShowAvatars ? "<div class=\"margin_left\"></div>" : "";
        sb.append("<div></div><div class=\"post_header\">\n");
        sb.append("\t<table width=\"100%\">\n");
        sb.append("\t\t<tr><td>").append(advDiv);

        if(msg.isCurator())
            sb.append("<span class=\"curator\"></span>");
        sb.append("<span class=\"").append(userState).append("\">").append(nickLink).append("</span></td>\n");
        sb.append("\t\t\t<td><div align=\"right\"><span class=\"post_date_cli\">").append(msg.getDate()).append("<a ")
                .append(getHtmlout(m_IsWebviewAllowJavascriptInterface, "showPostLinkMenu", msg.getId())).append(">#").append(msg.getNumber()).append("</a></span></div></td>\n");
        sb.append("\t\t</tr>\n");


        sb.append("<tr><td colspan=\"2\">");
        if (m_IsShowAvatars) {
            sb.append("<div class=\"avatar_parent_container\">");
            sb.append("<div class=\"avatar_container\">");
            String avatar = msg.getAvatarFileName();

//            if (!m_IsLoadImages)
//            {
//                sb.append("<a class=\"sp_img\" ")
//                        .append(TextUtils.isEmpty(avatar) ?
//                                "" : TopicBodyBuilder.getHtmlout(m_IsWebviewAllowJavascriptInterface, "showImgPreview", new String[]{"аватар", avatar, avatar}))
//                        .append(">аватар</a>");
//            } else {
            if (TextUtils.isEmpty(avatar))
                avatar = "file:///android_asset/profile/logo.png";
            sb.append("<img src=\"").append(avatar).append("\" />");
            //}
            sb.append("</div></div>");

        }
        sb.append("</td></tr>");

        String userGroup = msg.getUserGroup() == null ? "" : msg.getUserGroup();
        sb.append("<tr>\n" + "\t\t\t<td colspan=\"2\">").append(advDiv)
                .append("<span  class=\"user_group\">").append(userGroup)
                .append("</span>");

        sb.append("</td></tr>");
        sb.append("\t\t<tr>\n");
        sb.append("\t\t\t<td>").append(advDiv).append(TextUtils.isEmpty(msg.getUserId()) ? "" : getReputation(msg)).append("</td>\n");
        if (Client.getInstance().getLogined()) {
            String[] postMenuParams = {msg.getId(), msg.getDate(), msg.getUserId(), nickParam, msg.getCanEdit() ? "1" : "0", msg.getCanDelete() ? "1" : "0"};
            sb.append("\t\t\t<td><div align=\"right\">").append("<a ")
                    .append(getHtmlout(m_IsWebviewAllowJavascriptInterface, "showPostMenu", postMenuParams)).append(" class=\"system_link\">Меню</a></div></td>");
        }
        sb.append("\t\t</tr>");
        sb.append("\t</table>\n");
        sb.append("</div>\n");
    }

    private String getReputation(Post msg) {
        String[] params = new String[]{msg.getId(), msg.getUserId(), msg.getNickParam(), msg.getCanPlusRep() ? "1" : "0", msg.getCanMinusRep() ? "1" : "0"};
        //        if (!msg.getCanMinusRep() || !msg.getCanPlusRep())
        return "<a " + getHtmlout(m_IsWebviewAllowJavascriptInterface, "showRepMenu", params) + "  class=\"system_link\" ><span class=\"post_date_cli\">Реп(" + msg.getUserReputation() + ")</span></a>";
    }

    private void addFooter(StringBuilder sb, Post post) {
        sb.append("<div class=\"post_footer\">");
        if (m_Logined) {

            String nickParam = post.getNickParam();
            String postNumber = post.getNumber();
            try {
                postNumber = Integer.toString(Integer.parseInt(post.getNumber()) - 1);
            } catch (Throwable ignored) {
            }
            sb.append(String.format("<a class=\"system_link\" href=\"/forum/index.php?act=report&t=%s&p=%s&st=%s\"><span class=\"send_claim_button\"></span></a>",
                    m_Topic.getId(), post.getId(), postNumber));

            String insertNickText = String.format("[SNAPBACK]%s[/SNAPBACK] [B]%s,[/B] \\n",
                    post.getId(), nickParam);
            sb.append("<a class=\"system_link\" ")
                    .append(getHtmlout(m_IsWebviewAllowJavascriptInterface, "insertTextToPost", insertNickText))
                    .append("><span class=\"insert_nick_button\"></span></a>");


            String[] quoteParams = {m_Topic.getForumId(), m_Topic.getId(), post.getId(), post.getDate(), post.getUserId(), nickParam};
            sb.append("<a class=\"system_link\" ")
                    .append(getHtmlout(m_IsWebviewAllowJavascriptInterface, "quote", quoteParams))
                    .append("><span class=\"quote_button\"></span></a>");

            if (!Client.getInstance().UserId.equals(post.getUserId())) {
                sb.append("<a ").append(getHtmlout(m_IsWebviewAllowJavascriptInterface, "postVoteBad", post.getId()))
                        .append(" class=\"system_link\"><span class=\"post_vote_bad\"></span></a>");

                sb.append("<a ").append(getHtmlout(m_IsWebviewAllowJavascriptInterface, "postVoteGood", post.getId()))
                        .append(" class=\"system_link\"><span class=\"post_vote_good\"></span></a>");
            }
            if (post.getCanEdit())
                sb.append(String.format("<a class=\"system_link\" id=\"edit-but-%s\" href=\"/forum/index.php?act=post&do=edit_post&f=%s&t=%s&p=%s&st=%s\"><span class=\"edit_post_button\"></span></a>",
                        post.getId(), m_Topic.getForumId(), m_Topic.getId(), post.getId(), postNumber));
            if (post.getCanDelete())
                sb.append(String.format("<a class=\"system_link\" href=\"/forum/index.php?act=Mod&CODE=04&f=%s&t=%s&p=%s&st=%s&auth_key=%s\"><span class=\"delete_post_button\"></span></a>",
                        m_Topic.getForumId(), m_Topic.getId(), post.getId(), postNumber, m_Topic.getAuthKey()));

        }
        sb.append("</div>\n\n");
    }

    public ExtTopic getTopic() {
        return m_Topic;
    }

    public void setMMod(boolean MMod) {
        this.m_MMod = MMod;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean isMMod() {
        return m_MMod;
    }
}
