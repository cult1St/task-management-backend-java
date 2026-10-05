package com.task_management.first_backend.application.auth.services;

import com.task_management.first_backend.application.auth.dto.OtpResponseDTO;
import com.task_management.first_backend.application.auth.enums.OtpStatus;
import com.task_management.first_backend.application.auth.models.OtpCode;
import com.task_management.first_backend.application.auth.repository.OtpCodeRepository;
import com.task_management.first_backend.application.shared.helpers.CodeGenerator;
import com.task_management.first_backend.application.shared.helpers.HashString;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OtpCodeService {
    private final OtpCodeRepository codeRepository;
    private final HashString hashString;

    public OtpResponseDTO generateOtp(String email) {
        return generateOtp(email, false);
    }

    public OtpResponseDTO generateOtp(String email, boolean enforceRateLimit) {
        if (enforceRateLimit) {
            Optional<OtpCode> latest = codeRepository.findFirstByEmailOrderByCreatedAtDesc(email);
            if (latest.isPresent()) {
                LocalDateTime createdAt = latest.get().getCreatedAt();
                if (createdAt != null && createdAt.isAfter(LocalDateTime.now().minusSeconds(60))) {
                    throw new ResponseStatusException(
                            HttpStatus.TOO_MANY_REQUESTS,
                            "Please wait before requesting another verification code."
                    );
                }
            }
        }

        codeRepository.updateOthersToUsed(email, OtpStatus.USED);

        String otpCode = CodeGenerator.generateCode(6);
        String hashedCode = hashString.hash(otpCode);

        OtpCode otpCodeSave = OtpCode.builder()
                .email(email)
                .codeHash(hashedCode)
                .status(OtpStatus.PENDING)
                .expiryAt(LocalDateTime.now().plusSeconds(900))
                .build();
        codeRepository.save(otpCodeSave);
        return new OtpResponseDTO(otpCode, hashedCode);
    }

    public OtpCode verifyOtpCode(String otpCode) {
        String hashed = hashString.hash(otpCode);
        return codeRepository.getValidOtp(hashed, LocalDateTime.now(), OtpStatus.PENDING);
    }

    public boolean markAsVerified(String otpCode) {
        OtpCode validOtp = verifyOtpCode(otpCode);
        if (validOtp == null) {
            return false;
        }
        validOtp.setStatus(OtpStatus.USED);
        codeRepository.save(validOtp);
        return true;
    }
}
