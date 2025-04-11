package com.portalasig.ms.uaa.email.template;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PasswordRecoveryTemplate {

    private String title;
    private String target;
    private String primaryBody;
    private String secondaryBody;
    private String url;
    private String urlLabel;
    private String closingMessage;
}
