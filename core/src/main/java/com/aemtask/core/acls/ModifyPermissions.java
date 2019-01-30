package com.aemtask.core.acls;

import java.util.NoSuchElementException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.security.AccessControlList;
import javax.jcr.security.AccessControlManager;
import javax.jcr.security.AccessControlPolicyIterator;
import javax.jcr.security.Privilege;

import org.apache.sling.jcr.api.SlingRepository;
import org.apache.jackrabbit.api.security.JackrabbitAccessControlList;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ModifyPermissions {

    private static final String CONTENT_WE_RETAIL_FR = "/content/we-retail/fr";
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifyPermissions.class);
    private static final String DENY_ACCESS_USER_GROUP = "deny-access";
    private static final String ERROR_MSG_REPO_EXCEPTION = "**************************Repo Exception";
    private static final String INFO_MSG_MODIFY_PERMISSIONS_ACTIVATED = "ModifyPermissions activated";

    @Reference
    private SlingRepository slingRepository;

    @Activate
    protected void activate() {
        modifyPermissions();
        LOGGER.info(INFO_MSG_MODIFY_PERMISSIONS_ACTIVATED);
    }

    private void modifyPermissions() {
//        Session adminSession = null;
//        try {
//            adminSession = slingRepository.loginAdministrative(null); // deprecated, but part of the task self-check
//            UserManager userMgr =
//                    ((org.apache.jackrabbit.api.JackrabbitSession) adminSession).getUserManager();
//            AccessControlManager accessControlManager = adminSession.getAccessControlManager();
//            Authorizable denyAccess = userMgr.getAuthorizable(DENY_ACCESS_USER_GROUP);
//            AccessControlPolicyIterator policyIterator =
//                    accessControlManager.getApplicablePolicies(CONTENT_WE_RETAIL_FR);
//            AccessControlList accessControlList;
//            try {
//                accessControlList = (JackrabbitAccessControlList)
//                        policyIterator.nextAccessControlPolicy();
//            } catch (NoSuchElementException e) {
//                accessControlList = (JackrabbitAccessControlList)
//                        accessControlManager.getPolicies(CONTENT_WE_RETAIL_FR)[0];
//            }
//            Privilege[] privileges =
//                    {accessControlManager.privilegeFromName(Privilege.JCR_READ)};
//            accessControlList.addAccessControlEntry(denyAccess.getPrincipal(), privileges);
//            accessControlManager.setPolicy(CONTENT_WE_RETAIL_FR, accessControlList);
//            adminSession.save();
//        } catch (RepositoryException e) {
//            LOGGER.error(ERROR_MSG_REPO_EXCEPTION, e);
//        } finally {
//            if (adminSession != null) {
//                adminSession.logout();
//            }
//        }
    }
}