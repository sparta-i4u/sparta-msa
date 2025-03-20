package com.i4u.company.exceptiion;

import java.util.List;
import java.util.UUID;

public class CompanyNotFoundException extends RuntimeException {

    public CompanyNotFoundException(final UUID id) {
        super(String.format("해당 업체가 존재하지 않습니다. - 요청 정보 {\"companyId\": \"%s\"}", id));
    }

    public CompanyNotFoundException(final List<UUID> ids) {
        super(String.format("해당 업체들이 존재하지 않습니다. - 요청 정보 {\"companyIds\": %s}", ids));
    }
}
