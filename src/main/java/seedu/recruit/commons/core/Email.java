package seedu.recruit.commons.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Base64;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;

/**
 * Class for using GMAIL on RecruitBook
 */
public class Email {
    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final String APPLICATION_NAME = "CS2113 F09 T04";
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_COMPOSE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = Email.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .setApprovalPrompt("auto")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }
    /**
     * Initialiser for Gmail Service
     */
    public static Gmail init() throws IOException, GeneralSecurityException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        // Create a new authorized Gmail API client
        return new Gmail.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email recruit of the receiver
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    public static MimeMessage createEmail(String from,
                                          String to,
                                          String subject,
                                          String bodyText)
            throws MessagingException {
        Properties props;
        props = System.getProperties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId  User's email recruit. The special value "me"
     *                can be used to indicate the authenticated user.
     * @param email   Email to be sent.
     * @throws MessagingException
     * @throws IOException
     */
    public static void sendMessage(Gmail service, String userId, MimeMessage email)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(email);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
    }

    /**
     * Create a Message from an email
     *
     * @param email Email to be set to raw of message
     * @return Message containing base64url encoded email.
     * @throws IOException
     * @throws MessagingException
     */
    private static Message createMessageWithEmail(MimeMessage email) throws MessagingException, IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        email.writeTo(baos);
        String encodedEmail = Base64.encodeBase64URLSafeString(baos.toByteArray());
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }
}