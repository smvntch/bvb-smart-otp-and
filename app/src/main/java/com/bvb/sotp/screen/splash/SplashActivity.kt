package com.bvb.sotp.screen.splash

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import com.bvb.sotp.R
import com.bvb.sotp.mvp.MvpLoginActivity
import com.bvb.sotp.repository.AccountRepository
import com.bvb.sotp.screen.authen.login.LoginActivity
import com.bvb.sotp.screen.authen.pincode.CreatePinCodeActivity
import com.bvb.sotp.util.showRetryDialog
import com.centagate.module.common.Configuration
import com.centagate.module.device.FingerprintAuthentication
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.ByteArrayInputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.*
import java.util.concurrent.TimeUnit


class SplashActivity : MvpLoginActivity<SplashPresenterContract>(), SplashViewContract {
    private val timeout = 30 //in second, timeout of request

    override val layoutResId: Int
        get() = R.layout.activity_splash

    private val showPeepSubject: PublishSubject<Any> = PublishSubject.create()

    override fun initPresenter() {
        presenter = SplashPresenter(this)
    }

    override fun attachViewToPresenter() {
        presenter.attachView(this)
    }

    override fun showError(message: String) {
        showRetryDialog(this) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isTaskRoot) {
            finish()
            return
        }
    }

    override fun setupViews() {

        initSDK()
        AccountRepository.getInstance(this)
        startCount()

        if (TextUtils.isEmpty(preferenceHelper.getHid())) {
            val uniqueID = UUID.randomUUID().toString()
            preferenceHelper.setHid(uniqueID)

        }
    }

    fun startCount() {
        compositeDisposable.add(showPeepSubject.debounce(1000, TimeUnit.MILLISECONDS)
            .observeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                gotoNext()
            })
        showPeepSubject.onNext(true)

    }


    private fun initSDK() {
        //Setup the initialize of SDK
        Configuration.getInstance().webServiceUrl =
            applicationContext.resources.getString(R.string.app_default_web_service_url)
        Configuration.getInstance().approvalUrl =
            applicationContext.resources.getString(R.string.app_default_soft_cert_url)
        Configuration.getInstance().authenticationUrl =
            applicationContext.resources.getString(R.string.app_default_auth_url)

        Configuration.getInstance().timeout = timeout
        Configuration.getInstance().serverEncPublicKey =
            applicationContext.resources.getString(R.string.app_default_server_enc_public_key)
        Configuration.getInstance().serverSignPublicKey =
            applicationContext.resources.getString(R.string.app_default_server_verify_public_key)

        //trusted chain from BVB Production - update production value
        val bvbProdI =
            "MIIEfTCCA2WgAwIBAgIDG+cVMA0GCSqGSIb3DQEBCwUAMGMxCzAJBgNVBAYTAlVTMSEwHwYDVQQKExhUaGUgR28gRGFkZHkgR3JvdXAsIEluYy4xMTAvBgNVBAsTKEdvIERhZGR5IENsYXNzIDIgQ2VydGlmaWNhdGlvbiBBdXRob3JpdHkwHhcNMTQwMTAxMDcwMDAwWhcNMzEwNTMwMDcwMDAwWjCBgzELMAkGA1UEBhMCVVMxEDAOBgNVBAgTB0FyaXpvbmExEzARBgNVBAcTClNjb3R0c2RhbGUxGjAYBgNVBAoTEUdvRGFkZHkuY29tLCBJbmMuMTEwLwYDVQQDEyhHbyBEYWRkeSBSb290IENlcnRpZmljYXRlIEF1dGhvcml0eSAtIEcyMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv3FiCPH6WTT3G8kYo/eASVjpIoMTpsUgQwE7hPHmhUmfJ+r2hBtOoLTbcJjHMgGxBT4HTu70+k8vWTAi56sZVmvigAf88xZ1gDlRe+X5NbZ0TqmNghPktj+pA4P6or6KFWp/3gvDthkUBcrqw6gElDtGfDIN8wBmIsiNaW02jBEYt9OyHGC0OPoCjM7T3UYH3go+6118yHz7sCtTpJJiaVElBWEaRIGMLKlDliPfrDqBmg4pxRyp6V0etp6eMAo5zvGIgPtLXcwy7IViQyU0AlYnAZG0O3AqP26x6JyIAX2f1PnbU21gnb8s51iruF9G/M7EGwM8CetJMVxpRrPgRwIDAQABo4IBFzCCARMwDwYDVR0TAQH/BAUwAwEB/zAOBgNVHQ8BAf8EBAMCAQYwHQYDVR0OBBYEFDqahQcQZyi27/a9BUFuIMGU2g/eMB8GA1UdIwQYMBaAFNLEsNKR1EwRcbNhyz2h/t2oatTjMDQGCCsGAQUFBwEBBCgwJjAkBggrBgEFBQcwAYYYaHR0cDovL29jc3AuZ29kYWRkeS5jb20vMDIGA1UdHwQrMCkwJ6AloCOGIWh0dHA6Ly9jcmwuZ29kYWRkeS5jb20vZ2Ryb290LmNybDBGBgNVHSAEPzA9MDsGBFUdIAAwMzAxBggrBgEFBQcCARYlaHR0cHM6Ly9jZXJ0cy5nb2RhZGR5LmNvbS9yZXBvc2l0b3J5LzANBgkqhkiG9w0BAQsFAAOCAQEAWQtTvZKGEacke+1bMc8dH2xwxbhuvk679r6XUOEwf7ooXGKUwuN+M/f7QnaF25UcjCJYdQkMiGVnOQoWCcWgOJekxSOTP7QYpgEGRJHjp2kntFolfzq3Ms3dhP8qOCkzpN1nsoX+oYggHFCJyNwq9kIDN0zmiN/VryTyscPfzLXs4Jlet0lUIDyUGAzHHFIYSaRt4bNYC8nY7NmuHDKOKHAN4v6mF56ED71XcLNa6R+ghlO773z/aQvgSMO3kwvIClTErF0UZzdsyqUvMQg3qm5vjLyb4lddJIGvl5echK1srDdMZvNhkREg5L4wn3qkKQmw4TRfZHcYQFHfjDCmrw=="
        //trusted chain from BVB Production - update production value
        val bvbProdR =
            "MIIE0DCCA7igAwIBAgIBBzANBgkqhkiG9w0BAQsFADCBgzELMAkGA1UEBhMCVVMxEDAOBgNVBAgTB0FyaXpvbmExEzARBgNVBAcTClNjb3R0c2RhbGUxGjAYBgNVBAoTEUdvRGFkZHkuY29tLCBJbmMuMTEwLwYDVQQDEyhHbyBEYWRkeSBSb290IENlcnRpZmljYXRlIEF1dGhvcml0eSAtIEcyMB4XDTExMDUwMzA3MDAwMFoXDTMxMDUwMzA3MDAwMFowgbQxCzAJBgNVBAYTAlVTMRAwDgYDVQQIEwdBcml6b25hMRMwEQYDVQQHEwpTY290dHNkYWxlMRowGAYDVQQKExFHb0RhZGR5LmNvbSwgSW5jLjEtMCsGA1UECxMkaHR0cDovL2NlcnRzLmdvZGFkZHkuY29tL3JlcG9zaXRvcnkvMTMwMQYDVQQDEypHbyBEYWRkeSBTZWN1cmUgQ2VydGlmaWNhdGUgQXV0aG9yaXR5IC0gRzIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC54MsQ1K92vdSTYuswZLiBCGzDBNliF44v/z5lz4/OYuY8UhzaFkVLVat4a2ODYpDOD2lsmcgaFItMzEUz6ojcnqOvK/6AYZ15V8TPLvQ/MDxdR/yaFrzDN5ZBUY4RS1T4KL7QjL7wMDge87Am+GZHY23ecSZHjzhHU9FGHbTj3ADqRay9vHHZqm8A29vNMDp5T19MR/gd71vCxJ1gO7GyQ5HYpDNO6rPWJ0+tJYqlxvTV0KaudAVkV4i1RFXULSo6Pvi4vekyCgKUZMQWOlDxSq7neTOvDCAHf+jfBDnCaQJsY1L6d8EbyHSHyLmTGFBUNUtpTrw700kuH9zB0lL7AgMBAAGjggEaMIIBFjAPBgNVHRMBAf8EBTADAQH/MA4GA1UdDwEB/wQEAwIBBjAdBgNVHQ4EFgQUQMK9J47MNIMwojPX+2yz8LQsgM4wHwYDVR0jBBgwFoAUOpqFBxBnKLbv9r0FQW4gwZTaD94wNAYIKwYBBQUHAQEEKDAmMCQGCCsGAQUFBzABhhhodHRwOi8vb2NzcC5nb2RhZGR5LmNvbS8wNQYDVR0fBC4wLDAqoCigJoYkaHR0cDovL2NybC5nb2RhZGR5LmNvbS9nZHJvb3QtZzIuY3JsMEYGA1UdIAQ/MD0wOwYEVR0gADAzMDEGCCsGAQUFBwIBFiVodHRwczovL2NlcnRzLmdvZGFkZHkuY29tL3JlcG9zaXRvcnkvMA0GCSqGSIb3DQEBCwUAA4IBAQAIfmyTEMg4uJapkEv/oV9PBO9sPpyIBslQj6Zz91cxG7685C/b+LrTW+C05+Z5Yg4MotdqY3MxtfWoSKQ7CC2iXZDXtHwlTxFWMMS2RJ17LJ3lXubvDGGqv+QqG+6EnriDfcFDzkSnE3ANkR/0yBOtg2DZ2HKocyQetawiDsoXiWJYRBuriSUBAA/NxBti21G00w9RKpv0vHP8ds42pM3Z2Czqrpv1KrKQ0U11GIo/ikGQI31bS/6kA1ibRrLDYGCD+H1QQc7CoZDDu+8CL9IVVO5EFdkKrqeKM+2xLXY2JtwE65/3YR8V3Idv7kaWKK2hJn0KCacuBKONvPi8BDAB"

        val Securemetric_internal_ROOT_CA =
            "MIIDUzCCAjugAwIBAgIUMqXNErW2CsWjJIwbKTheas0wOJowDQYJKoZIhvcNAQELBQAwMTEQMA4GA1UEAwwHU1NMQ09SUDEQMA4GA1UECgwHU1NMQ09SUDELMAkGA1UEBhMCVVMwHhcNMjAwNzIxMTc0ODI5WhcNMzAwNzE5MTc0ODI5WjAxMRAwDgYDVQQDDAdTU0xDT1JQMRAwDgYDVQQKDAdTU0xDT1JQMQswCQYDVQQGEwJVUzCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANTS3ky4MmD30JF+Hyx8XGgVyEskpCuifIzunEyrTVpNyhFDjAG7s3jQw2CDKA6cl9y4jj9qKafLIzRx/xtT9k6uCecJKOys88hMiw0/JQnSaItVy4rkb4PTJtKszAn3UyyKTBp38nLkINrX8yJSQcBRCxZ/cM7AA6dlcTSkjwkhRs1g/eXhkdxwhGzE0fRr5NpEwKLmo7EJftG8foaGp1e5cmKEezrMPhU8ilV9e0RrA5zkJr//JW3qzBh0Hdf/MF73/i53Hr73o92Fp9b8ePcES2rKMPCkg13+G7f32+RkooyzF1nlp4nE1RzjXPnwIciEL6rXEciLt17/5eCqymMCAwEAAaNjMGEwDwYDVR0TAQH/BAUwAwEB/zAfBgNVHSMEGDAWgBSGkQCnHV46cOSYegnDfP7r81W5SjAdBgNVHQ4EFgQUhpEApx1eOnDkmHoJw3z+6/NVuUowDgYDVR0PAQH/BAQDAgGGMA0GCSqGSIb3DQEBCwUAA4IBAQB7UJvMWDJ2LlLxh2eIbp9Xd6PaIUCgxAmWB6UzgnVe30Zqn8H5DQao6HbI/Ov98dP6MK00m93ZIUPS93o9vBjNdaKDLGWcRgJHnVSgQkI3vyBAVTHxEJ3la/tumCcxFDyeKwzDKU32nRpVQKXrFssdW6jAYr8lQP56agrSkJWFdsnqWqbOOO5hyI42tKc4kf9qvdr81GyQ1WRgYsklDim9hIVZNqerkEFn++nBDrVwqOxZyyBbJ4rPYwRQ4VZuNObp4w/5+30x51HXy5zugw1FuDngpso9J6TAPRx5pe07fXlKt+Q2tdz0GDhRtzTn/j/2cDKQ6bK+ULGnuJTbJFz2"

        //trsuted chain from 118.70.13.108:3443
        val Securemetric_internal_SSL_CA =
            "MIIDSzCCAjOgAwIBAgIUHBDkhfBl7KMAu29YEwvXEwlXkakwDQYJKoZIhvcNAQELBQAwMTEQMA4GA1UEAwwHU1NMQ09SUDEQMA4GA1UECgwHU1NMQ09SUDELMAkGA1UEBhMCVVMwHhcNMjAwOTE2MTcyNzM1WhcNMjMwNjEzMTcyNzM1WjApMQwwCgYDVQQDDANTU0wxDDAKBgNVBAoMA1NTTDELMAkGA1UEBhMCVk4wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC2P8R9/TkKP5NLwZBuVXlLlQ/Q9Ry2JGm1DW2Kd4Ljdl86SgQXe1GQOsVGBPqb/dYJLi++As6nwStPZqjpmG7Oh2fumzmxbv7ZR8N8qPjduVXNnoZ+y42ZN4kST+F6Y29S6qnEwmY+tXYLRnqrXX+Z4ai6W79P8X4uW6/6fF2mgfb+1z4rIU/6mMvlbyaYDmEU7T58qHzTU44Dy/36nER2JSs7sC7ScJjJhyL2uhvkj7Q6ruiZCAowXQ/7UrF70XCKW4xSbqaagGfYQE/y+++PYDZ36GVJe2VQo0J7EItThImrTKFXxXaHzDTkOn+CHY660rDJi4ng8Z3yTBPwCi2dAgMBAAGjYzBhMA8GA1UdEwEB/wQFMAMBAf8wHwYDVR0jBBgwFoAUhpEApx1eOnDkmHoJw3z+6/NVuUowHQYDVR0OBBYEFEwHk+DZ6bkkY8a5c8dDQ/xYex05MA4GA1UdDwEB/wQEAwIBhjANBgkqhkiG9w0BAQsFAAOCAQEAIf1kPVa74ZNV4v+DXPx5hFwSmZkZfAA1c7WX8J/zeoP5ulCZeW+DH+qN+QD0cPUHpgnk4QTlo6KO+GOJaeIKEIyLvodx+N2tacUKHQ78J9VOnUe3AmSDobN99vQ845lHQtYFANwD8JUwbyf5M9t+7Xhe9EDgIHBWqum4UbHnhHSebMIvzdf7E1lmxtPYpn0CKzQsCA/44BygzpU5+trN7Q1lQX0kZ7rFJXc9ILAi5bGpDp8dMcPREJy+o9NPPafG3EFV4N44oamNNvIvh3YtzDP00rGFsEnuCzKLfIWeUGFNzYbb8HWLSF6HlxyPzpyeExKatz1eFHVOr2SsudPVFg=="

        var keyStore: KeyStore? = null
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore!!.load(null, null)

            val cf = CertificateFactory.getInstance("X.509")

            val securemetric_internal_ROOT_CA = cf.generateCertificate(
                ByteArrayInputStream(
                    Base64.decode(
                        Securemetric_internal_ROOT_CA,
                        Base64.DEFAULT
                    )
                )
            )
            keyStore.setCertificateEntry(
                "securemetric_internal_ROOT_CA",
                securemetric_internal_ROOT_CA
            )

            val securemetric_internal_SSL_CA = cf.generateCertificate(
                ByteArrayInputStream(
                    Base64.decode(
                        Securemetric_internal_SSL_CA,
                        Base64.DEFAULT
                    )
                )
            )
            keyStore.setCertificateEntry(
                "securemetric_internal_SSL_CA",
                securemetric_internal_SSL_CA
            )

            val bvbProdICA = cf.generateCertificate(
                ByteArrayInputStream(
                    Base64.decode(
                        bvbProdI,
                        Base64.DEFAULT
                    )
                )
            )
            keyStore.setCertificateEntry("go_Daddy_Root_CA", bvbProdICA)

            val bvbProdRCA = cf.generateCertificate(
                ByteArrayInputStream(
                    Base64.decode(
                        bvbProdR,
                        Base64.DEFAULT
                    )
                )
            )
            keyStore.setCertificateEntry("go_Daddy_Secure_CA", bvbProdRCA)




        } catch (e: Exception) {

        }

        //the certs which are trusted to has connection, will reject if doesn't match with server cert
        Configuration.getInstance().connectionKeyStore = keyStore

        //if want to truested all connection to server
        //Configuration.getInstance().setConnectionKeyStore(null);

    }


    override fun gotoNext() {
        val authentication = AccountRepository.getInstance(this).authentication
        val account = AccountRepository.getInstance(this).accounts
        if (authentication != null && account.value != null && account.value?.size!! > 0) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
            return
        } else {
            val intent = Intent(this, CreatePinCodeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

    }

    override fun onStartListen() {
        TODO("Not yet implemented")
    }

    override fun onAuthenticatedSuccess(fprint: FingerprintAuthentication?) {
        TODO("Not yet implemented")
    }

    override fun onAuthenticatedError(
        fprint: FingerprintAuthentication?,
        p0: Int,
        p1: CharSequence?
    ) {
        TODO("Not yet implemented")
    }

}