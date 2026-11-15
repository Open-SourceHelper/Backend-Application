package com.neurosync.kinemo.child.application;

import java.security.Principal;

/** Supplied by the authentication layer after verifying the account and its email. */
public interface VerifiedChildAccount extends Principal {
    String verifiedEmail();
}
