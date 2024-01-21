package com.bvb.sotp

object Constant {

    /**
     * The Centagate SDK uses this filename to store the OTP seed.
     */
    const val FILENAME = "smvn_sotp"
    const val ERROR_DATA_NULL = 1000
    const val Intv = 30
    const val IntvMili = 30000
    const val maxDayLoginPin = 30
    const val tryLimit = 5
    const val tryLimitFinger = 10
    const val maxDayChangePin = 365

    const val TYPE_QA = "QA"
    const val TYPE_MANUAL = "Manual"
    const val TYPE_TERM = "Terms"

   // 1:mobile push,2:transaction,3:mobile push invalid,4: invalid active code,5:invalid qr
    const val NOTI_TYPE_MOBILE_PUSH = "1"
    const val NOTI_TYPE_TRANSACTION = "2"
    const val NOTI_TYPE_INVALID_MOBILE_PUSH = "3"
    const val NOTI_TYPE_INVALID_ACTIVE_CODE = "4"
    const val NOTI_TYPE_INVALID_QR = "5"

    const val mmsCert = "MIIHejCCBmKgAwIBAgIMBxpAtVkJqoeiSEydMA0GCSqGSIb3DQEBCwUAMGYxCzAJ" +
            "BgNVBAYTAkJFMRkwFwYDVQQKExBHbG9iYWxTaWduIG52LXNhMTwwOgYDVQQDEzNH" +
            "bG9iYWxTaWduIE9yZ2FuaXphdGlvbiBWYWxpZGF0aW9uIENBIC0gU0hBMjU2IC0g" +
            "RzIwHhcNMTgwODEzMDIxNzAyWhcNMjAwOTE5MDkzNjM3WjCBozELMAkGA1UEBhMC" +
            "Vk4xDjAMBgNVBAgMBUhhbm9pMQ4wDAYDVQQHDAVIYW5vaTESMBAGA1UECwwJSVQg" +
            "Q2VudGVyMUEwPwYDVQQKDDhKT0lOVCBTVE9DSyBDT01NRVJDSUFMIEJBTksgRk9S" +
            "IEZPUkVJR04gVFJBREUgT0YgVklFVE5BTTEdMBsGA1UEAwwUKi52aWV0Y29tYmFu" +
            "ay5jb20udm4wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDD1/6i741Z" +
            "u4376Osd0ScblQ/Vn5ld3B0z5zyPZb9Uzhxo8xjUOpmZ6W5FV+t1Wf/BEfq78Vf+" +
            "0AGJDmjgLBK4XS1LfDAt8fPRiCKM+fd2NGSBQ/r3OuLcK83utDay1WV+aGuIzxN0" +
            "nXah3CoHLdSh7bPesC8dJurBwyf8Y+5snMevxTFBrvT9Ye5IJZzLslbIex+w84Nh" +
            "4vM058601rQNRVkK46lkP1wsyCy3WTzXnAl2l73eE46iKNXSftfvAVwzOYwxrHz0" +
            "COKQNUZDSGmH0YtiP/x/GrwnJJzByWKDzcA6XZEdleoX75H1HR5Yi17VpxAaO6qR" +
            "oPFZ+0wkZCuvAgMBAAGjggPoMIID5DAOBgNVHQ8BAf8EBAMCBaAwgaAGCCsGAQUF" +
            "BwEBBIGTMIGQME0GCCsGAQUFBzAChkFodHRwOi8vc2VjdXJlLmdsb2JhbHNpZ24u" +
            "Y29tL2NhY2VydC9nc29yZ2FuaXphdGlvbnZhbHNoYTJnMnIxLmNydDA/BggrBgEF" +
            "BQcwAYYzaHR0cDovL29jc3AyLmdsb2JhbHNpZ24uY29tL2dzb3JnYW5pemF0aW9u" +
            "dmFsc2hhMmcyMFYGA1UdIARPME0wQQYJKwYBBAGgMgEUMDQwMgYIKwYBBQUHAgEW" +
            "Jmh0dHBzOi8vd3d3Lmdsb2JhbHNpZ24uY29tL3JlcG9zaXRvcnkvMAgGBmeBDAEC" +
            "AjAJBgNVHRMEAjAAMEkGA1UdHwRCMEAwPqA8oDqGOGh0dHA6Ly9jcmwuZ2xvYmFs" +
            "c2lnbi5jb20vZ3MvZ3Nvcmdhbml6YXRpb252YWxzaGEyZzIuY3JsMIGfBgNVHREE" +
            "gZcwgZSCFCoudmlldGNvbWJhbmsuY29tLnZugh9hdXRvZGlzY292ZXIudmlldGNv" +
            "bWJhbmsuY29tLnZughdtYWlsLnZpZXRjb21iYW5rLmNvbS52boIWb3dhLnZpZXRj" +
            "b21iYW5rLmNvbS52boIWd3d3LnZpZXRjb21iYW5rLmNvbS52boISdmlldGNvbWJh" +
            "bmsuY29tLnZuMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjAdBgNVHQ4E" +
            "FgQUCWydQB0deKHPX7Ltj/JgelWHahYwHwYDVR0jBBgwFoAUlt5h8b0cFilTHMDM" +
            "fTuDAEDmGnwwggF+BgorBgEEAdZ5AgQCBIIBbgSCAWoBaAB2AFWB1MIWkDYBSuoL" +
            "m1c8U/DA5Dh4cCUIFy+jqh0HE9MMAAABZTESjeMAAAQDAEcwRQIhAIJi9amFfawX" +
            "EAoxX9peptuzmZ9bYkSURmNYGOeCmzEoAiAamM+jC0YV7GfR9lEnBJlHX8vMrEoo" +
            "XdqvFf+y1JE4bwB2AId1v+dZfPiMQ5lfvfNu/1aNR1Y2/0q1YMG06v9eoIMPAAAB" +
            "ZTESkKoAAAQDAEcwRQIgI4QwkQ1vbvXL/jXbp7DReNxIrG1zoDGtSCgjfOXVty8C" +
            "IQD2HZLXPBvqsH+OS6pmgfMSqva/0z0T71zjadCII7E8QQB2AKS5CZC0GFgUh7sT" +
            "osxncAo8NZgE+RvfuON3zQ7IDdwQAAABZTESkSkAAAQDAEcwRQIgKSPgGgT0RjWg" +
            "XvGW/QSH8ZbmpXREqNGeqmkIJoYN3VACIQDFs8k6RrcthfxRIStaKbMmYphmd2+b" +
            "Ifzc5xCzVgquUjANBgkqhkiG9w0BAQsFAAOCAQEAqmuVoGk31cvRGti7rmk37afh" +
            "+CSGCb688D9DKsoihRkvfFrsuSYN6VIoapa6Cd8aKeefCQt/nhbsrVmT+ZdUK+Uk" +
            "jY3DjKa55imF3rPPgJyBv+Tnwwt2uyWcm++q7M9Y3XaisxcYALikWYx5OmMH1u1+" +
            "vUVgGD/XRa0XuERQ9yX/0l4954XeZLQ3C8cSSzhKfT2yqE5wHJJeerKO2yfjSii/" +
            "0arekeHOamRMlghJZiMqthmv9HS698juTYbixYd7Ol8/FjtnU1x0fJNdHNT2PYPv" +
            "fJz8V8aKNsdfSSczYmEOC/eSdw9IcyQa0uoyDsxn6RYNsj4Gkkfsa3B86xSlqQ=="

    const val otpCert = "MIIHezCCBmOgAwIBAgIMeG8HXgtQ658O5ZAtMA0GCSqGSIb3DQEBCwUAMGYxCzAJ" +
            "BgNVBAYTAkJFMRkwFwYDVQQKExBHbG9iYWxTaWduIG52LXNhMTwwOgYDVQQDEzNH" +
            "bG9iYWxTaWduIE9yZ2FuaXphdGlvbiBWYWxpZGF0aW9uIENBIC0gU0hBMjU2IC0g" +
            "RzIwHhcNMTgwODE1MTYxNzA1WhcNMjAwOTE5MDkzNjM3WjCBozELMAkGA1UEBhMC" +
            "Vk4xDjAMBgNVBAgMBUhhbm9pMQ4wDAYDVQQHDAVIYW5vaTESMBAGA1UECwwJSVQg" +
            "Q2VudGVyMUEwPwYDVQQKDDhKT0lOVCBTVE9DSyBDT01NRVJDSUFMIEJBTksgRk9S" +
            "IEZPUkVJR04gVFJBREUgT0YgVklFVE5BTTEdMBsGA1UEAwwUKi52aWV0Y29tYmFu" +
            "ay5jb20udm4wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDac9TW5xNS" +
            "sUCrHL1aCqLOUbutz+tN1eiRHe/OMsJd614e6iqVsUvbE4O1NmiC//g90RNF+oqz" +
            "jh+0Jhwu0o9OYAeGgf0/yvPftOEJJJRpr9Dv+s5RGqknVt9pd6Pcd5YPrRb7melV" +
            "z2hBGTwdYd7r8yDIH47mymeaYxx5JP8h9r2525y9vrJtPOLmnrbbb1q1z05AX6Yh" +
            "xNLJOKjOK32t0+evY0z71l9XYQ9tOOE29CXQtFRXyjUx1zZYA7UgQuBvheZ0LA8j" +
            "pVtHEskYXruBy1hNEXP0hTlwQkO6zOg56vFUSZtvoX2UnK8MPYzji0jkhDkPZZXp" +
            "OFSjsH2hNphJAgMBAAGjggPpMIID5TAOBgNVHQ8BAf8EBAMCBaAwgaAGCCsGAQUF" +
            "BwEBBIGTMIGQME0GCCsGAQUFBzAChkFodHRwOi8vc2VjdXJlLmdsb2JhbHNpZ24u" +
            "Y29tL2NhY2VydC9nc29yZ2FuaXphdGlvbnZhbHNoYTJnMnIxLmNydDA/BggrBgEF" +
            "BQcwAYYzaHR0cDovL29jc3AyLmdsb2JhbHNpZ24uY29tL2dzb3JnYW5pemF0aW9u" +
            "dmFsc2hhMmcyMFYGA1UdIARPME0wQQYJKwYBBAGgMgEUMDQwMgYIKwYBBQUHAgEW" +
            "Jmh0dHBzOi8vd3d3Lmdsb2JhbHNpZ24uY29tL3JlcG9zaXRvcnkvMAgGBmeBDAEC" +
            "AjAJBgNVHRMEAjAAMEkGA1UdHwRCMEAwPqA8oDqGOGh0dHA6Ly9jcmwuZ2xvYmFs" +
            "c2lnbi5jb20vZ3MvZ3Nvcmdhbml6YXRpb252YWxzaGEyZzIuY3JsMIGfBgNVHREE" +
            "gZcwgZSCFCoudmlldGNvbWJhbmsuY29tLnZugh9hdXRvZGlzY292ZXIudmlldGNv" +
            "bWJhbmsuY29tLnZughdtYWlsLnZpZXRjb21iYW5rLmNvbS52boIWb3dhLnZpZXRj" +
            "b21iYW5rLmNvbS52boIWd3d3LnZpZXRjb21iYW5rLmNvbS52boISdmlldGNvbWJh" +
            "bmsuY29tLnZuMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjAdBgNVHQ4E" +
            "FgQUGiUGBiPVm97s6lpJVsxe51AMpF4wHwYDVR0jBBgwFoAUlt5h8b0cFilTHMDM" +
            "fTuDAEDmGnwwggF/BgorBgEEAdZ5AgQCBIIBbwSCAWsBaQB3AFWB1MIWkDYBSuoL" +
            "m1c8U/DA5Dh4cCUIFy+jqh0HE9MMAAABZT5gWXsAAAQDAEgwRgIhAMPGsiVTQyJU" +
            "KRdeUuvoJIvbXT0r9yFsAs8yRFsAlm2jAiEAsYej3O5m4z4OAAoTcVem1hc6tDsd" +
            "SJ6vCl7cjHq+Pl4AdgCHdb/nWXz4jEOZX73zbv9WjUdWNv9KtWDBtOr/XqCDDwAA" +
            "AWU+YFlxAAAEAwBHMEUCIQCGL5+Tafwakq+bKGbUfvA/dPw/goBAZwOkbHi8LP57" +
            "ZwIgRq22wunZoIdIrVil9aLHa7X0FGcGNSe54RT1NLSNLn4AdgCkuQmQtBhYFIe7" +
            "E6LMZ3AKPDWYBPkb37jjd80OyA3cEAAAAWU+YFzGAAAEAwBHMEUCIGs7x8fSy7HZ" +
            "XqXXIDpDGRU/hZhIdviPdbftK7JEMlPTAiEA6E+VDxI36kalEfZuYymi1rjC4W14" +
            "ss3aol29D4tta/cwDQYJKoZIhvcNAQELBQADggEBAK/l9MSCTaEwM0OXhLqS8HqS" +
            "oykpTC+82RMWAmL8ihB0OsmnC2THwj35OWh/xQCcyEu+ruw3M/S5xKFgVx5j45w6" +
            "AfbyB/eEYLdnWTD/WPCD/fRGdcg7LZJfV1QyOOvUOHwBn+PJVy7UjoAqREeWx17V" +
            "6l6bZwVxjY0+Xr+26EcOWN4+M3Y8SLePLk/LYOnyta/64Ye5+NRe/UELTCTvU8bU" +
            "msSP+ELFcJneY52fwuEgMuvszPFMuzSs/D7XSJgO63GeqgRuYQmhsHEI2s3cOOpf" +
            "Bj9lfb20iTXgBwCHt2S2N8vvV39p0eCzdSit9Ai2bujMpDMHfpRwehTS0/qV878="

    const val Pilot_Inter_Cert = "MIIEaTCCA1GgAwIBAgILBAAAAAABRE7wQkcwDQYJKoZIhvcNAQELBQAwVzELMAkG" +
            "A1UEBhMCQkUxGTAXBgNVBAoTEEdsb2JhbFNpZ24gbnYtc2ExEDAOBgNVBAsTB1Jv" +
            "b3QgQ0ExGzAZBgNVBAMTEkdsb2JhbFNpZ24gUm9vdCBDQTAeFw0xNDAyMjAxMDAw" +
            "MDBaFw0yNDAyMjAxMDAwMDBaMGYxCzAJBgNVBAYTAkJFMRkwFwYDVQQKExBHbG9i" +
            "YWxTaWduIG52LXNhMTwwOgYDVQQDEzNHbG9iYWxTaWduIE9yZ2FuaXphdGlvbiBW" +
            "YWxpZGF0aW9uIENBIC0gU0hBMjU2IC0gRzIwggEiMA0GCSqGSIb3DQEBAQUAA4IB" +
            "DwAwggEKAoIBAQDHDmw/I5N/zHClnSDDDlM/fsBOwphJykfVI+8DNIV0yKMCLkZc" +
            "C33JiJ1Pi/D4nGyMVTXbv/Kz6vvjVudKRtkTIso21ZvBqOOWQ5PyDLzm+ebomchj" +
            "SHh/VzZpGhkdWtHUfcKc1H/hgBKueuqI6lfYygoKOhJJomIZeg0k9zfrtHOSewUj" +
            "mxK1zusp36QUArkBpdSmnENkiN74fv7j9R7l/tyjqORmMdlMJekYuYlZCa7pnRxt" +
            "Nw9KHjUgKOKv1CGLAcRFrW4rY6uSa2EKTSDtc7p8zv4WtdufgPDWi2zZCHlKT3hl" +
            "2pK8vjX5s8T5J4BO/5ZS5gIg4Qdz6V0rvbLxAgMBAAGjggElMIIBITAOBgNVHQ8B" +
            "Af8EBAMCAQYwEgYDVR0TAQH/BAgwBgEB/wIBADAdBgNVHQ4EFgQUlt5h8b0cFilT" +
            "HMDMfTuDAEDmGnwwRwYDVR0gBEAwPjA8BgRVHSAAMDQwMgYIKwYBBQUHAgEWJmh0" +
            "dHBzOi8vd3d3Lmdsb2JhbHNpZ24uY29tL3JlcG9zaXRvcnkvMDMGA1UdHwQsMCow" +
            "KKAmoCSGImh0dHA6Ly9jcmwuZ2xvYmFsc2lnbi5uZXQvcm9vdC5jcmwwPQYIKwYB" +
            "BQUHAQEEMTAvMC0GCCsGAQUFBzABhiFodHRwOi8vb2NzcC5nbG9iYWxzaWduLmNv" +
            "bS9yb290cjEwHwYDVR0jBBgwFoAUYHtmGkUNl8qJUC99BM00qP/8/UswDQYJKoZI" +
            "hvcNAQELBQADggEBAEYq7l69rgFgNzERhnF0tkZJyBAW/i9iIxerH4f4gu3K3w4s" +
            "32R1juUYcqeMOovJrKV3UPfvnqTgoI8UV6MqX+x+bRDmuo2wCId2Dkyy2VG7EQLy" +
            "XN0cvfNVlg/UBsD84iOKJHDTu/B5GqdhcIOKrwbFINihY9Bsrk8y1658GEV1BSl3" +
            "30JAZGSGvip2CTFvHST0mdCF/vIhCPnG9vHQWe3WVjwIKANnuvD58ZAWR65n5ryA" +
            "SOlCdjSXVWkkDoPWoC209fN5ikkodBpBocLTJIg1MGCUF7ThBCIxPTsvFwayuJ2G" +
            "K1pp74P1S8SqtCr4fKGxhZSM9AyHDPSsQPhZSZg="

    const val Pilot_Root_Cert = "MIIDdTCCAl2gAwIBAgILBAAAAAABFUtaw5QwDQYJKoZIhvcNAQEFBQAwVzELMAkG" +
            "A1UEBhMCQkUxGTAXBgNVBAoTEEdsb2JhbFNpZ24gbnYtc2ExEDAOBgNVBAsTB1Jv" +
            "b3QgQ0ExGzAZBgNVBAMTEkdsb2JhbFNpZ24gUm9vdCBDQTAeFw05ODA5MDExMjAw" +
            "MDBaFw0yODAxMjgxMjAwMDBaMFcxCzAJBgNVBAYTAkJFMRkwFwYDVQQKExBHbG9i" +
            "YWxTaWduIG52LXNhMRAwDgYDVQQLEwdSb290IENBMRswGQYDVQQDExJHbG9iYWxT" +
            "aWduIFJvb3QgQ0EwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDaDuaZ" +
            "jc6j40+Kfvvxi4Mla+pIH/EqsLmVEQS98GPR4mdmzxzdzxtIK+6NiY6arymAZavp" +
            "xy0Sy6scTHAHoT0KMM0VjU/43dSMUBUc71DuxC73/OlS8pF94G3VNTCOXkNz8kHp" +
            "1Wrjsok6Vjk4bwY8iGlbKk3Fp1S4bInMm/k8yuX9ifUSPJJ4ltbcdG6TRGHRjcdG" +
            "snUOhugZitVtbNV4FpWi6cgKOOvyJBNPc1STE4U6G7weNLWLBYy5d4ux2x8gkasJ" +
            "U26Qzns3dLlwR5EiUWMWea6xrkEmCMgZK9FGqkjWZCrXgzT/LCrBbBlDSgeF59N8" +
            "9iFo7+ryUp9/k5DPAgMBAAGjQjBAMA4GA1UdDwEB/wQEAwIBBjAPBgNVHRMBAf8E" +
            "BTADAQH/MB0GA1UdDgQWBBRge2YaRQ2XyolQL30EzTSo//z9SzANBgkqhkiG9w0B" +
            "AQUFAAOCAQEA1nPnfE920I2/7LqivjTFKDK1fPxsnCwrvQmeU79rXqoRSLblCKOz" +
            "yj1hTdNGCbM+w6DjY1Ub8rrvrTnhQ7k4o+YviiY776BQVvnGCv04zcQLcFGUl5gE" +
            "38NflNUVyRRBnMRddWQVDf9VMOyGj/8N7yy5Y0b2qvzfvGn9LhJIZJrglfCm7ymP" +
            "AbEVtQwdpf5pLGkkeB6zpxxxYu7KyJesF12KwvhHhm4qxFYxldBniYUr+WymXUad" +
            "DKqC5JlR3XC321Y9YeRq4VzW9v493kHMB65jUr9TU/Qr6cf9tveCX4XSQRjbgbME" +
            "HMUfpIBvFSDJ3gyICh3WZlXi/EjJKSZp4A=="


}