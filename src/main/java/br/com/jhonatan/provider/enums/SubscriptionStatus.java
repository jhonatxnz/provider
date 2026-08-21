package br.com.jhonatan.provider.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum SubscriptionStatus {

    INACTIVE("0"),
    ACTIVE("1");

    private final String id;

    public String value() { return id; }
}
