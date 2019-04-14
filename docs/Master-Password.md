---

layout: default
title: Master Password
resource: true
categories: [Other]

---

## Master password

To connect to databases you need passwords and DBTarzan saves the databases' passwords in the **connections.config** file, together with the other connection data (driver, url, user, etc.).
Passwords are saved encrypted using the [AES](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard) (AES/CBC/PKCS5PADDING) algorithm.
Before version 1.19 the safety of the password encryption was quite limited: the encryption key to encrypt/decrypt these passwords was stored in code.
To solve once for all this problem, version 1.19 gives the possibility to [choose](Global-settings) **your own encryption key** (called in the UI **master password**) to encrypt/decrypt all these passwords.

The master password is not stored anywhere, what is stored in the global configuration file is what we call **verification key**.
The verification key is the result of encoding a **specific password** (in the code) using the given encryption key. If decoding the verification key using the encryption key gives the original specific password, the encryption key is correct and can be used to decrypt the databases' passwords.

In other words, the verification key is used to test the encryption key correctness before it gets used to decrypt the databases' passwords. If the encryption key is incorrect, testing it against the verification key results in a unambiguous error message. 

Changing the master password results in changes in the connections.config file, because the passwords are re-encrypted with the new master password.
Therefore please, before you change the master password, **make a copy of the connections.config file**, so that you are able to come back to the previous version of the master password (or no master password if you was not using one) if something goes wrong.
Once you are confortable with the new master password, remove the connections.config copy.