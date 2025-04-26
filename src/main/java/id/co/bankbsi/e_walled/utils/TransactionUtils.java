package id.co.bankbsi.e_walled.utils;

import id.co.bankbsi.e_walled.models.Transactions;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class TransactionUtils {
    @Autowired
    private JavaMailSender javaMailSender;

    @Transactional
    public void imageReceipt(Transactions transaction) {

        BufferedImage imageFile = generateReceiptImage(transaction);
        try {
            String receiptImageUrl = saveReceiptImage(imageFile, transaction.getTransactionNumber());
            transaction.setReceiptImage(receiptImageUrl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sendInlineReceiptEmail(transaction.getWallet().getUser().getEmail(), imageFile);

    }

    public String saveReceiptImage(BufferedImage image, String transactionNumber) throws IOException {
        // Define your target directory
        String uploadDir = System.getProperty("user.dir") + "/assets/images/receipts/";
        Files.createDirectories(Paths.get(uploadDir)); // create folder if it doesn't exist

        // Generate filename
        String filename = transactionNumber + ".png";
        Path filePath = Paths.get(uploadDir, filename);

        // Save image to file
        ImageIO.write(image, "png", filePath.toFile());

        // Build public URL to serve this image
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        return baseUrl + "/assets/images/receipts/" + filename;
    }

    public BufferedImage generateReceiptImage(Transactions tx) {
        BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 400, 300);

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.drawString("Transaction Receipt", 120, 30);
        g.drawString("From: " + tx.getWallet().getNumber(), 20, 70);
        g.drawString("To: " + tx.getAssociateWallet().getNumber(), 20, 100);
        g.drawString("Amount: Rp " + tx.getAmount(), 20, 130);
        g.drawString("Date: " + tx.getCreatedAt(), 20, 160);
        g.drawString("Description: " + tx.getDescription(), 20, 190);

        g.dispose();
        return image;
    }

    @Async
    public void sendInlineReceiptEmail(String toEmail, BufferedImage receiptImage) {
        try {
            // Convert image to byte array in-memory
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(receiptImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = getMimeMessageHelper(toEmail, message);

            // Embed the image inline
            helper.addInline("receiptImg", new ByteArrayResource(imageBytes), "image/png");

            javaMailSender.send(message);
        } catch (Exception e) {
            return;
        }

    }

    private static MimeMessageHelper getMimeMessageHelper(String toEmail, MimeMessage message) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Your Transaction Receipt");

        // Email HTML with inline image reference (cid:receiptImg)
        String htmlMsg = """
                    <html>
                        <body>
                            <p>Dear Customer,</p>
                            <p>Here is your transaction receipt:</p>
                            <img src='cid:receiptImg' alt='Transaction Receipt'/>
                            <p>Thank you for using our service.</p>
                        </body>
                    </html>
                """;
        helper.setText(htmlMsg, true); // Enable HTML
        return helper;
    }
}
