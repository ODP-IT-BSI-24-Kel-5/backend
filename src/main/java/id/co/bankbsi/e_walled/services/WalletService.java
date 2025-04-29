package id.co.bankbsi.e_walled.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import id.co.bankbsi.e_walled.dto.request.CreateWalletRequest;
import id.co.bankbsi.e_walled.dto.request.UpdateWalletRequest;
import id.co.bankbsi.e_walled.dto.response.WalletResponseGeneral;
import id.co.bankbsi.e_walled.dto.response.Response;
import id.co.bankbsi.e_walled.dto.response.WalletResponse;
import id.co.bankbsi.e_walled.dto.response.WalletsResponse;
import id.co.bankbsi.e_walled.exceptions.BadRequestException;
import id.co.bankbsi.e_walled.exceptions.NotFoundException;
import id.co.bankbsi.e_walled.models.Users;
import id.co.bankbsi.e_walled.models.Wallets;
import id.co.bankbsi.e_walled.repositories.UserRepository;
import id.co.bankbsi.e_walled.repositories.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final ModelMapper modelMapper;
    //    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    private final WalletRepository walletRepository;

    public Response getWallet(Users user) {
        try {
            List<Wallets> wallets = walletRepository.findByUserIdOrderByIsMainDescCreatedAtAsc(user.getId());

            if (wallets.isEmpty()) {
                throw new NotFoundException("No wallets found for user ID: " + user.getId());
            }

            return WalletsResponse.success(wallets);
        } catch (Exception e) {
            throw e;
        }
    }

    public Response getMainWallet(Users user) {
        try {
            Wallets wallets = walletRepository.findFirstByUserIdAndIsMain(user.getId(), true);

            if (wallets == null) {
                throw new NotFoundException("Wallet not found!I");
            }

            return WalletResponse.success(wallets);
        } catch (Exception e) {
            throw e;
        }
    }

    public Response getLatestTransfer(Users user) {
        try {
            List<Wallets> wallets = walletRepository.findLatestTransactions(user);
            List<WalletResponseGeneral.WalletsGeneral> walletsGenerals = new ArrayList<>();
            for (Wallets wallet : wallets) {
                walletsGenerals.add(modelMapper.map(wallet, WalletResponseGeneral.WalletsGeneral.class));
            }
            return WalletResponseGeneral.WalletsResponseGeneral.success(walletsGenerals);
        } catch (Exception e) {
            throw e;
        }
    }

    public Response getSpecificWallet(Users user, String number) {
        try {
            Wallets wallets = walletRepository.findFirstByUserIdAndNumber(user.getId(), number);

            if (wallets == null) {
                throw new NotFoundException("Wallet not found!");
            }

            return WalletResponse.success(wallets);
        } catch (Exception e) {
            throw e;
        }
    }

    public Response getSpecificWalletGeneral(String number) {
        try {
            Wallets wallets = walletRepository.findByNumber(number);

            if (wallets == null) {
                throw new NotFoundException("Wallet not found!");
            }

            return WalletResponseGeneral.success(new WalletResponseGeneral.WalletsGeneral(wallets.getName(), wallets.getUser().getFullName(), wallets.getNumber()));
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public WalletResponse createWallet(Users userData, CreateWalletRequest createWalletRequest) {

        try {
            Users user = userRepository.findById(userData.getId());

            List<Wallets> wallets = walletRepository.findByUserId(userData.getId());
            if (user == null) {
                throw new NotFoundException("User not found!");
            }
            if (wallets.size() > 4) {
                throw new BadRequestException("Can't create more than 5 wallets!");
            }
            for (Wallets wallet : wallets) {
                if (createWalletRequest.getName().equals(wallet.getName())) {
                    throw new BadRequestException("You have wallet with the same name!");
                }
            }
            return WalletResponse.successCreate(insertWallet(user, createWalletRequest, false));
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public WalletResponse updateWallet(Users userData, String number, UpdateWalletRequest updateWalletRequest) {

        try {
            Users user = userRepository.findById(userData.getId());
            if (user == null) {
                throw new NotFoundException("User not found!");
            }

            Wallets wallets = walletRepository.findFirstByUserIdAndNumber(userData.getId(), number);
            if (wallets == null) {
                throw new BadRequestException("Wallets not found!");
            }
            List<Wallets> walletd = walletRepository.findByUserId(userData.getId());
            for (Wallets wallet : walletd) {
                if (updateWalletRequest.getName().equals(wallet.getName())) {
                    throw new BadRequestException("You have wallet with the same name!");
                }
            }
            wallets.setName(updateWalletRequest.getName());
            walletRepository.updateWalletName(wallets.getId(), wallets.getName());

            return WalletResponse.successCreate(wallets);
        } catch (Exception e) {
            throw e;
        }
    }


    @Transactional
    public Wallets insertWallet(Users user, CreateWalletRequest createWalletRequest, boolean isRegister) {
        Wallets wallet = new Wallets();
        wallet.setUser(user);

        if (createWalletRequest.getMain() != null && createWalletRequest.getMain()) {
            walletRepository.removeMain(user);
            wallet.setIsMain(true);
        }

        wallet.setName("My Wallet");
        if (!(createWalletRequest.getName() == null || createWalletRequest.getName() == "")) {
            wallet.setName(createWalletRequest.getName());
        }

        String generatedAccountNumber;
        do {
            generatedAccountNumber = generateRandomAccountNumber();
        } while (walletRepository.existsByNumber(generatedAccountNumber));

        wallet.setNumber(generatedAccountNumber);  // Set the unique account number
        return walletRepository.save(wallet);  // Save the wallet
    }

    public byte[] generateQr(Users userData, String wallet) throws WriterException, IOException {

        Wallets walletData = walletRepository.findByNumber(wallet);

        if (walletData == null) {
            throw new NotFoundException("Wallet not found!");
        }

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode("byondwallet://open/transactions/transfer?" + walletData.getId().toString(), BarcodeFormat.QR_CODE, 400, 400);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return outputStream.toByteArray();
    }


    private String generateRandomAccountNumber() {
        String prefix = "9072";  // The fixed prefix you want (can be anything)
        String randomDigits = generateRandomDigits(6);
        return prefix + randomDigits;  // Concatenate prefix and random digits
    }


    private String generateRandomDigits(int length) {
        Random random = new Random();
        StringBuilder digits = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            digits.append(random.nextInt(10));  // Random digit from 0 to 9
        }
        return digits.toString();
    }
}
