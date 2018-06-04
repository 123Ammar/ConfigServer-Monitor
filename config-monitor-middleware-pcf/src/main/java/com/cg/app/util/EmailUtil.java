package com.cg.app.util;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Component
public class EmailUtil {
	
	@Autowired
	JavaMailSender sender;

	
	@Value("${mail.from:}")
	String from;
	

	@Value("${mail.to:}")
	String to;
	

	@Value("${mail.subject:}")
	String subject;
	
	

	public void sendEmail(String body ) {
		SimpleMailMessage mail = new SimpleMailMessage();
		 
		String[] toSplit = to.split(",");
		
		mail.setFrom(from);
		mail.setTo(toSplit);
		mail.setSubject(subject);
		mail.setText(body);

	
		try {
		System.out.println("Sending mail...");
		sender.send(mail);
		System.out.println(" Mail send successfully ");
		}catch (Exception e) {
			System.out.println("Failed Sending Mail");
		}

	}

}
