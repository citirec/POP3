package POP3;

import java.io.FileOutputStream;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream;  
import java.util.Enumeration;  
import java.util.Properties;  
  
import javax.mail.BodyPart;  
import javax.mail.Folder;  
import javax.mail.Message;  
import javax.mail.MessagingException;  
import javax.mail.Multipart;  
import javax.mail.Session;  
import javax.mail.Store;  
import javax.mail.internet.MimeMultipart;  
  
public class POP3 {  
  
    public static void main(String args[]) throws MessagingException,  
            IOException {  
        Properties props = new Properties();  
        props.setProperty("mail.store.protocol", "pop3");  
        props.setProperty("mail.pop3.host", "pop3.163.com");  
        Session session = Session.getDefaultInstance(props);  
        Store store = session.getStore("pop3");  
        store.connect("XXXX@163.com", "password");  
        Folder folder = store.getFolder("INBOX");  
        folder.open(Folder.READ_WRITE);  
  
        // 全部邮件数  
        int messageCount = folder.getMessageCount();  
        System.out.println(messageCount);  
        Message[] messages = folder.getMessages();  
        for (int i = 0; i < messages.length; i++) {  
            Message message = messages[i];  
            System.out.println(message.getSubject());   
            System.out.println("发送时间：" + message.getSentDate());  
            System.out.println("主题：" + message.getSubject());  
            System.out.println("内容：" + message.getContent());  
            
            //获取所有的Header，头信息  
            Enumeration headers = message.getAllHeaders();  
              
            //解析邮件内容  
            Object content = message.getContent();  
            if (content instanceof MimeMultipart) {  
                MimeMultipart multipart = (MimeMultipart) content;  
                parseMultipart(multipart);  
            }  
            System.out  
                    .println("========================================================");  
            System.out  
                    .println("========================================================");  
  
        }  
  
        folder.close(true);  
        store.close();  
    }  
      
    //对复杂邮件的解析 

    public static void parseMultipart(Multipart multipart) throws MessagingException, IOException {  
        int count = multipart.getCount();  
        System.out.println("couont =  "+count);  
        for (int idx=0;idx<count;idx++) {  
            BodyPart bodyPart = multipart.getBodyPart(idx);  
            System.out.println(bodyPart.getContentType());  
            if (bodyPart.isMimeType("text/plain")) {  
                System.out.println("plain................."+bodyPart.getContent());  
            } else if(bodyPart.isMimeType("text/html")) {  
                System.out.println("html..................."+bodyPart.getContent());  
            } else if(bodyPart.isMimeType("multipart/*")) {  
                Multipart mpart = (Multipart)bodyPart.getContent();  
                parseMultipart(mpart);  
                  
            } else if (bodyPart.isMimeType("application/octet-stream")) {  
                String disposition = bodyPart.getDisposition();  
                System.out.println(disposition);  
                if (disposition.equalsIgnoreCase(BodyPart.ATTACHMENT)) {  
                    String fileName = bodyPart.getFileName();  
                    InputStream is = bodyPart.getInputStream();  
                    copy(is, new FileOutputStream("D:\\"+fileName));  
                }  
            }  
        }  
    }  
      
    // 文件拷贝，在用户进行附件下载的时候，可以把附件的InputStream传给用户进行下载  
     public static void copy(InputStream is, OutputStream os) throws IOException {  
        byte[] bytes = new byte[1024];  
        int len = 0;  
        while ((len=is.read(bytes)) != -1 ) {  
            os.write(bytes, 0, len);  
        }  
        if (os != null)  
            os.close();  
        if (is != null)  
            is.close();  
    }    
}  
