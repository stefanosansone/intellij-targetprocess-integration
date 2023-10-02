package com.github.stefanosansone.intellijtargetprocessintegration.util

import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.generateServiceName


fun createCredentialAttributes(key: String): CredentialAttributes {
    return CredentialAttributes(
        generateServiceName("MySystem", key)
    )
}