/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.Address
 *  javax.mail.Authenticator
 *  javax.mail.BodyPart
 *  javax.mail.Message
 *  javax.mail.Message$RecipientType
 *  javax.mail.Multipart
 *  javax.mail.Session
 *  javax.mail.Transport
 *  javax.mail.internet.AddressException
 *  javax.mail.internet.InternetAddress
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMessage
 *  javax.mail.internet.MimeMultipart
 */
package ch.qos.logback.core.net;

import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.boolex.EvaluationException;
import ch.qos.logback.core.boolex.EventEvaluator;
import ch.qos.logback.core.helpers.CyclicBuffer;
import ch.qos.logback.core.net.LoginAuthenticator;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import ch.qos.logback.core.sift.DefaultDiscriminator;
import ch.qos.logback.core.sift.Discriminator;
import ch.qos.logback.core.spi.CyclicBufferTracker;
import ch.qos.logback.core.util.ContentTypeUtil;
import ch.qos.logback.core.util.OptionHelper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.InitialContext;

public abstract class SMTPAppenderBase<E>
extends AppenderBase<E> {
    static InternetAddress[] EMPTY_IA_ARRAY = new InternetAddress[0];
    static final long MAX_DELAY_BETWEEN_STATUS_MESSAGES = 1228800000L;
    long lastTrackerStatusPrint = 0L;
    long delayBetweenStatusMessages = 300000L;
    protected Layout<E> subjectLayout;
    protected Layout<E> layout;
    private List<PatternLayoutBase<E>> toPatternLayoutList = new ArrayList<PatternLayoutBase<E>>();
    private String from;
    private String subjectStr = null;
    private String smtpHost;
    private int smtpPort = 25;
    private boolean starttls = false;
    private boolean ssl = false;
    private boolean sessionViaJNDI = false;
    private String jndiLocation = "java:comp/env/mail/Session";
    String username;
    String password;
    String localhost;
    boolean asynchronousSending = true;
    private String charsetEncoding = "UTF-8";
    protected Session session;
    protected EventEvaluator<E> eventEvaluator;
    protected Discriminator<E> discriminator = new DefaultDiscriminator();
    protected CyclicBufferTracker<E> cbTracker;
    private int errorCount = 0;

    protected abstract Layout<E> makeSubjectLayout(String var1);

    @Override
    public void start() {
        if (this.cbTracker == null) {
            this.cbTracker = new CyclicBufferTracker();
        }
        this.session = this.sessionViaJNDI ? this.lookupSessionInJNDI() : this.buildSessionFromProperties();
        if (this.session == null) {
            this.addError("Failed to obtain javax.mail.Session. Cannot start.");
            return;
        }
        this.subjectLayout = this.makeSubjectLayout(this.subjectStr);
        this.started = true;
    }

    private Session lookupSessionInJNDI() {
        this.addInfo("Looking up javax.mail.Session at JNDI location [" + this.jndiLocation + "]");
        try {
            InitialContext initialContext = new InitialContext();
            Object obj = initialContext.lookup(this.jndiLocation);
            return (Session)obj;
        }
        catch (Exception e) {
            this.addError("Failed to obtain javax.mail.Session from JNDI location [" + this.jndiLocation + "]");
            return null;
        }
    }

    private Session buildSessionFromProperties() {
        Properties props = new Properties(OptionHelper.getSystemProperties());
        if (this.smtpHost != null) {
            props.put("mail.smtp.host", this.smtpHost);
        }
        props.put("mail.smtp.port", Integer.toString(this.smtpPort));
        if (this.localhost != null) {
            props.put("mail.smtp.localhost", this.localhost);
        }
        LoginAuthenticator loginAuthenticator = null;
        if (this.username != null) {
            loginAuthenticator = new LoginAuthenticator(this.username, this.password);
            props.put("mail.smtp.auth", "true");
        }
        if (this.isSTARTTLS() && this.isSSL()) {
            this.addError("Both SSL and StartTLS cannot be enabled simultaneously");
        } else {
            if (this.isSTARTTLS()) {
                props.put("mail.smtp.starttls.enable", "true");
            }
            if (this.isSSL()) {
                String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
                props.put("mail.smtp.socketFactory.port", Integer.toString(this.smtpPort));
                props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
                props.put("mail.smtp.socketFactory.fallback", "true");
            }
        }
        return Session.getInstance((Properties)props, (Authenticator)loginAuthenticator);
    }

    @Override
    protected void append(E eventObject) {
        long now;
        String key;
        block9: {
            if (!this.checkEntryConditions()) {
                return;
            }
            key = this.discriminator.getDiscriminatingValue(eventObject);
            now = System.currentTimeMillis();
            CyclicBuffer cb = (CyclicBuffer)this.cbTracker.getOrCreate(key, now);
            this.subAppend(cb, eventObject);
            try {
                if (this.eventEvaluator.evaluate(eventObject)) {
                    CyclicBuffer cbClone = new CyclicBuffer(cb);
                    cb.clear();
                    if (this.asynchronousSending) {
                        SenderRunnable senderRunnable = new SenderRunnable(cbClone, eventObject);
                        this.context.getExecutorService().execute(senderRunnable);
                    } else {
                        this.sendBuffer(cbClone, eventObject);
                    }
                }
            }
            catch (EvaluationException ex) {
                ++this.errorCount;
                if (this.errorCount >= 4) break block9;
                this.addError("SMTPAppender's EventEvaluator threw an Exception-", ex);
            }
        }
        if (this.eventMarksEndOfLife(eventObject)) {
            this.cbTracker.endOfLife(key);
        }
        this.cbTracker.removeStaleComponents(now);
        if (this.lastTrackerStatusPrint + this.delayBetweenStatusMessages < now) {
            this.addInfo("SMTPAppender [" + this.name + "] is tracking [" + this.cbTracker.getComponentCount() + "] buffers");
            this.lastTrackerStatusPrint = now;
            if (this.delayBetweenStatusMessages < 1228800000L) {
                this.delayBetweenStatusMessages *= 4L;
            }
        }
    }

    protected abstract boolean eventMarksEndOfLife(E var1);

    protected abstract void subAppend(CyclicBuffer<E> var1, E var2);

    public boolean checkEntryConditions() {
        if (!this.started) {
            this.addError("Attempting to append to a non-started appender: " + this.getName());
            return false;
        }
        if (this.eventEvaluator == null) {
            this.addError("No EventEvaluator is set for appender [" + this.name + "].");
            return false;
        }
        if (this.layout == null) {
            this.addError("No layout set for appender named [" + this.name + "]. For more information, please visit http://logback.qos.ch/codes.html#smtp_no_layout");
            return false;
        }
        return true;
    }

    @Override
    public synchronized void stop() {
        this.started = false;
    }

    InternetAddress getAddress(String addressStr) {
        try {
            return new InternetAddress(addressStr);
        }
        catch (AddressException e) {
            this.addError("Could not parse address [" + addressStr + "].", e);
            return null;
        }
    }

    private List<InternetAddress> parseAddress(E event) {
        int len = this.toPatternLayoutList.size();
        ArrayList<InternetAddress> iaList = new ArrayList<InternetAddress>();
        for (int i = 0; i < len; ++i) {
            try {
                PatternLayoutBase<E> emailPL = this.toPatternLayoutList.get(i);
                String emailAdrr = emailPL.doLayout(event);
                if (emailAdrr == null || emailAdrr.length() == 0) continue;
                InternetAddress[] tmp = InternetAddress.parse((String)emailAdrr, (boolean)true);
                iaList.addAll(Arrays.asList(tmp));
                continue;
            }
            catch (AddressException e) {
                this.addError("Could not parse email address for [" + this.toPatternLayoutList.get(i) + "] for event [" + event + "]", e);
                return iaList;
            }
        }
        return iaList;
    }

    public List<PatternLayoutBase<E>> getToList() {
        return this.toPatternLayoutList;
    }

    protected void sendBuffer(CyclicBuffer<E> cb, E lastEventObject) {
        try {
            String footer;
            String presentationHeader;
            MimeBodyPart part = new MimeBodyPart();
            StringBuffer sbuf = new StringBuffer();
            String header = this.layout.getFileHeader();
            if (header != null) {
                sbuf.append(header);
            }
            if ((presentationHeader = this.layout.getPresentationHeader()) != null) {
                sbuf.append(presentationHeader);
            }
            this.fillBuffer(cb, sbuf);
            String presentationFooter = this.layout.getPresentationFooter();
            if (presentationFooter != null) {
                sbuf.append(presentationFooter);
            }
            if ((footer = this.layout.getFileFooter()) != null) {
                sbuf.append(footer);
            }
            String subjectStr = "Undefined subject";
            if (this.subjectLayout != null) {
                int newLinePos;
                subjectStr = this.subjectLayout.doLayout(lastEventObject);
                int n = newLinePos = subjectStr != null ? subjectStr.indexOf(10) : -1;
                if (newLinePos > -1) {
                    subjectStr = subjectStr.substring(0, newLinePos);
                }
            }
            MimeMessage mimeMsg = new MimeMessage(this.session);
            if (this.from != null) {
                mimeMsg.setFrom((Address)this.getAddress(this.from));
            } else {
                mimeMsg.setFrom();
            }
            mimeMsg.setSubject(subjectStr, this.charsetEncoding);
            List<InternetAddress> destinationAddresses = this.parseAddress(lastEventObject);
            if (destinationAddresses.isEmpty()) {
                this.addInfo("Empty destination address. Aborting email transmission");
                return;
            }
            Object[] toAddressArray = destinationAddresses.toArray(EMPTY_IA_ARRAY);
            mimeMsg.setRecipients(Message.RecipientType.TO, (Address[])toAddressArray);
            String contentType = this.layout.getContentType();
            if (ContentTypeUtil.isTextual(contentType)) {
                part.setText(sbuf.toString(), this.charsetEncoding, ContentTypeUtil.getSubType(contentType));
            } else {
                part.setContent((Object)sbuf.toString(), this.layout.getContentType());
            }
            MimeMultipart mp = new MimeMultipart();
            mp.addBodyPart((BodyPart)part);
            mimeMsg.setContent((Multipart)mp);
            mimeMsg.setSentDate(new Date());
            this.addInfo("About to send out SMTP message \"" + subjectStr + "\" to " + Arrays.toString(toAddressArray));
            Transport.send((Message)mimeMsg);
        }
        catch (Exception e) {
            this.addError("Error occurred while sending e-mail notification.", e);
        }
    }

    protected abstract void fillBuffer(CyclicBuffer<E> var1, StringBuffer var2);

    public String getFrom() {
        return this.from;
    }

    public String getSubject() {
        return this.subjectStr;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setSubject(String subject) {
        this.subjectStr = subject;
    }

    public void setSMTPHost(String smtpHost) {
        this.setSmtpHost(smtpHost);
    }

    public void setSmtpHost(String smtpHost) {
        this.smtpHost = smtpHost;
    }

    public String getSMTPHost() {
        return this.getSmtpHost();
    }

    public String getSmtpHost() {
        return this.smtpHost;
    }

    public void setSMTPPort(int port) {
        this.setSmtpPort(port);
    }

    public void setSmtpPort(int port) {
        this.smtpPort = port;
    }

    public int getSMTPPort() {
        return this.getSmtpPort();
    }

    public int getSmtpPort() {
        return this.smtpPort;
    }

    public String getLocalhost() {
        return this.localhost;
    }

    public void setLocalhost(String localhost) {
        this.localhost = localhost;
    }

    public CyclicBufferTracker<E> getCyclicBufferTracker() {
        return this.cbTracker;
    }

    public void setCyclicBufferTracker(CyclicBufferTracker<E> cbTracker) {
        this.cbTracker = cbTracker;
    }

    public Discriminator<E> getDiscriminator() {
        return this.discriminator;
    }

    public void setDiscriminator(Discriminator<E> discriminator) {
        this.discriminator = discriminator;
    }

    public boolean isAsynchronousSending() {
        return this.asynchronousSending;
    }

    public void setAsynchronousSending(boolean asynchronousSending) {
        this.asynchronousSending = asynchronousSending;
    }

    public void addTo(String to) {
        if (to == null || to.length() == 0) {
            throw new IllegalArgumentException("Null or empty <to> property");
        }
        PatternLayoutBase<E> plb = this.makeNewToPatternLayout(to.trim());
        plb.setContext(this.context);
        plb.start();
        this.toPatternLayoutList.add(plb);
    }

    protected abstract PatternLayoutBase<E> makeNewToPatternLayout(String var1);

    public List<String> getToAsListOfString() {
        ArrayList<String> toList = new ArrayList<String>();
        for (PatternLayoutBase<E> plb : this.toPatternLayoutList) {
            toList.add(plb.getPattern());
        }
        return toList;
    }

    public boolean isSTARTTLS() {
        return this.starttls;
    }

    public void setSTARTTLS(boolean startTLS) {
        this.starttls = startTLS;
    }

    public boolean isSSL() {
        return this.ssl;
    }

    public void setSSL(boolean ssl) {
        this.ssl = ssl;
    }

    public void setEvaluator(EventEvaluator<E> eventEvaluator) {
        this.eventEvaluator = eventEvaluator;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCharsetEncoding() {
        return this.charsetEncoding;
    }

    public String getJndiLocation() {
        return this.jndiLocation;
    }

    public void setJndiLocation(String jndiLocation) {
        this.jndiLocation = jndiLocation;
    }

    public boolean isSessionViaJNDI() {
        return this.sessionViaJNDI;
    }

    public void setSessionViaJNDI(boolean sessionViaJNDI) {
        this.sessionViaJNDI = sessionViaJNDI;
    }

    public void setCharsetEncoding(String charsetEncoding) {
        this.charsetEncoding = charsetEncoding;
    }

    public Layout<E> getLayout() {
        return this.layout;
    }

    public void setLayout(Layout<E> layout) {
        this.layout = layout;
    }

    class SenderRunnable
    implements Runnable {
        final CyclicBuffer<E> cyclicBuffer;
        final E e;

        SenderRunnable(CyclicBuffer<E> cyclicBuffer, E e) {
            this.cyclicBuffer = cyclicBuffer;
            this.e = e;
        }

        @Override
        public void run() {
            SMTPAppenderBase.this.sendBuffer(this.cyclicBuffer, this.e);
        }
    }
}

