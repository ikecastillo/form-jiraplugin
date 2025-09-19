package com.switchhr.jsm.model;

import net.java.ao.Entity;
import net.java.ao.Preload;
import net.java.ao.schema.Indexed;
import net.java.ao.schema.NotNull;
import net.java.ao.schema.StringLength;
import net.java.ao.schema.Table;

import java.util.Date;

@Table("AUDIT_RECORD")
@Preload
public interface AuditRecord extends Entity {

    @NotNull
    @StringLength(100)
    String getUserKey();
    void setUserKey(String userKey);

    @NotNull
    @StringLength(255)
    String getUserName();
    void setUserName(String userName);

    @StringLength(100)
    String getPortalId();
    void setPortalId(String portalId);

    @NotNull
    @StringLength(100)
    String getAction();
    void setAction(String action);

    @StringLength(StringLength.UNLIMITED)
    String getDetails();
    void setDetails(String details);

    @NotNull
    @Indexed
    Date getTimestamp();
    void setTimestamp(Date timestamp);

    @StringLength(50)
    String getIpAddress();
    void setIpAddress(String ipAddress);

    @StringLength(500)
    String getUserAgent();
    void setUserAgent(String userAgent);

    @StringLength(100)
    String getIssueKey();
    void setIssueKey(String issueKey);
}
