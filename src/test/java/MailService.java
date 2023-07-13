import jdk.nashorn.internal.objects.annotations.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

@SpringBootTest
public class MailService {


    private JavaMailSender javaMailSender;


    @Test
    void setJavaMailSender() {
        //given
        MailVO vo = new MailVO();
        vo.setAddress("piano4889@gmail.com");
        vo.setTitle("메일 테스트");
        vo.setMessage("메일 테스트 하고 있습니다~~~");

        //when

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(vo.getAddress());
        message.setSubject(vo.getTitle());
        message.setText(vo.getMessage());
        //then
        javaMailSender.send(message);
    }


}

