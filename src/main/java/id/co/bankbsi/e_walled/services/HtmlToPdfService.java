package id.co.bankbsi.e_walled.services;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import id.co.bankbsi.e_walled.exceptions.NotFoundException;
import id.co.bankbsi.e_walled.models.Transactions;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.models.Wallets;
import id.co.bankbsi.e_walled.repositories.TransactionRepository;
import id.co.bankbsi.e_walled.repositories.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class HtmlToPdfService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private WalletRepository walletRepository;

    public ByteArrayOutputStream generateWalletStatement(Users user, String wallet, Integer month, Integer year) throws Exception {
        try {
            Wallets wallets = walletRepository.findByNumberAndUserId(wallet, user.getId());

            if (!wallet.isEmpty() && wallets == null) {
                throw new NotFoundException("Wallet not found!");
            }
            List<Transactions> transactions = transactionRepository.findByWalletInMonth(user, wallets, month, year);

            StringBuilder htmlBuilder = new StringBuilder();

            htmlBuilder.append("<!DOCTYPE html><html><head><meta charset='UTF-8'/><title>Wallet e-Statement</title>")
                    .append("<style>")
                    .append("body { font-family: 'Arial', sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }")
                    .append("h2 { text-align: center; color: #333; font-size: 24px; margin-bottom: 20px; padding-bottom: 10px; border-bottom: 2px solid #444; }")
                    .append(".summary { background-color: #fff; padding: 20px; margin: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }")
                    .append(".summary p { font-size: 14px; color: #333; line-height: 1.6; margin: 5px 0; }")
                    .append("table { width: 100%; border-collapse: collapse; margin: 20px 0; }")
                    .append("table, th, td { border: 1px solid #ddd; }")
                    .append("th { background-color: #f9f9f9; color: #333; padding: 12px 15px; text-align: left; font-size: 14px; }")
                    .append("td { padding: 10px 15px; text-align: left; font-size: 14px; color: #333; }")
                    .append("tr:nth-child(even) { background-color: #f9f9f9; }")
                    .append("tr:hover { background-color: #f1f1f1; }")
                    .append(".footer { font-size: 12px; color: #777; text-align: center; margin-top: 40px; padding: 20px; }")
                    .append(".footer p { margin: 0; }")
                    .append("</style></head><body>")
                    .append("<h2>Wallet e-Statement - ").append(Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH)).append(" ").append(year).append("</h2>")
                    .append("<div class='summary'>");

            if (wallets != null) {
                htmlBuilder.append("<p><strong>Account ID:</strong> ").append(wallets.getNumber()).append("</p>")
                        .append("<p><strong>Owner:</strong>").append(wallets.getUser().getFullName()).append("</p>");
            }

            htmlBuilder.append("<p><strong>Statement Period:</strong> ").append(Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH)).append(" ").append(year).append("</p>")
                    .append("<p><strong>Generated On:</strong> ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"))).append("</p>")
                    .append("</div>")
                    .append("<table><thead><tr><th>Date</th><th>Description</th><th>Type</th><th>Amount</th><th>Balance After</th></tr></thead><tbody>");

            // Loop through the transactions and dynamically add rows
            for (Transactions transaction : transactions) {
                Long balanceLeft = (long) 0;
                Long amount = (long) 0;
                // Calculate the balance after each transaction
                balanceLeft = transaction.getWalletBalanceLeft();
                if (transaction.isDebit()) {
                    amount = transaction.getAmount();
                } else {
                    amount = transaction.getAmount() * -1;
                }

                // Add the transaction details to the table
                htmlBuilder.append("<tr>")
                        .append("<td>").append(transaction.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm"))).append("</td>")
                        .append("<td>").append(transaction.getNotes()).append("</td>")
                        .append("<td>").append(transaction.getType()).append("</td>")
                        .append("<td>").append(amount).append("</td>")
                        .append("<td>").append(balanceLeft).append("</td>")
                        .append("</tr>");
            }

            htmlBuilder.append("</tbody></table>")
                    .append("<div class='footer'>This is a system-generated statement. Please contact support for any discrepancies.</div>")
                    .append("</body></html>");

            // Generate PDF from HTML
            return generatePdfFromHtml(htmlBuilder.toString());
        } catch (Exception e) {
            throw e;
        }
    }

    private ByteArrayOutputStream generatePdfFromHtml(String htmlContent) throws Exception {
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withHtmlContent(htmlContent, null);
        builder.toStream(pdfOutputStream);
        builder.run();
        return pdfOutputStream;
    }
}
