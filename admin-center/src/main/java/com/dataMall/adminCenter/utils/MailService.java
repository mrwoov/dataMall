package com.dataMall.adminCenter.utils;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 邮件业务类
 */
@Service
public class MailService {
    private final Logger logger = LoggerFactory.getLogger(MailService.class);

    /**
     * 注入邮件工具类
     */
    @Resource
    private JavaMailSenderImpl javaMailSender;

    @Resource
    private EmailCode emailCode;

    @Value("${spring.mail.username}")
    private String sendMailer;

    public void sendCodeMessage(String to, String act) {
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        emailCode.create(to, String.valueOf(code));
        String msg = "你正在" + act + ",验证码为：" + code + ",验证码5分钟内有效";
        sendTextMailMessage(to, "数据交易平台", msg);
    }

    /**
     * 发送纯文本邮件
     *
     * @param to：收件人
     * @param subject：主题
     * @param text：文本
     */
    public void sendTextMailMessage(String to, String subject, String text) {

        try {
            //true 代表支持复杂的类型
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true);
            //邮件发信人
            mimeMessageHelper.setFrom(sendMailer);
            //邮件收信人  1或多个
            mimeMessageHelper.setTo(to.split(","));
            //邮件主题
            mimeMessageHelper.setSubject(subject);
            //邮件内容
            mimeMessageHelper.setText(text);
            //邮件发送时间
            mimeMessageHelper.setSentDate(new Date());

            //发送邮件
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            System.out.println("发送邮件成功：" + sendMailer + "->" + to);

        } catch (MessagingException e) {
            logger.error(e.getMessage());
            System.out.println("发送邮件失败：" + e.getMessage());
        }
    }
}

