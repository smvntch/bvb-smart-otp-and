package com.bvb.sotp.screen.splash

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
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
import com.centagate.module.log.Logger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.io.ByteArrayInputStream
import java.io.File
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.*
import java.util.concurrent.TimeUnit
import android.content.pm.PackageManager

import android.os.Build
import androidx.fragment.app.FragmentActivity
import android.widget.Toast

import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission


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
        Configuration.getInstance().integrationKey =
            applicationContext.resources.getString(R.string.app_integration_key)
        //trusted chain from BVB Production - update production value

        val bvbProdI =
            "MIIEYTCCA0mgAwIBAgIOSKQC3SeSDaIINJ3RmXswDQYJKoZIhvcNAQELBQAwTDEg" +
                    "MB4GA1UECxMXR2xvYmFsU2lnbiBSb290IENBIC0gUjMxEzARBgNVBAoTCkdsb2Jh" +
                    "bFNpZ24xEzARBgNVBAMTCkdsb2JhbFNpZ24wHhcNMTYwOTIxMDAwMDAwWhcNMjYw" +
                    "OTIxMDAwMDAwWjBiMQswCQYDVQQGEwJCRTEZMBcGA1UEChMQR2xvYmFsU2lnbiBu" +
                    "di1zYTE4MDYGA1UEAxMvR2xvYmFsU2lnbiBFeHRlbmRlZCBWYWxpZGF0aW9uIENB" +
                    "IC0gU0hBMjU2IC0gRzMwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCr" +
                    "awNnVNXcEfvFohPBjBkn3BB04mGDPfqO24+lD+SpvkY/Ar5EpAkcJjOfR0iBFYhW" +
                    "N80HzpXYy2tIA7mbXpKu2JpmYdU1xcoQpQK0ujE/we+vEDyjyjmtf76LLqbOfuq3" +
                    "xZbSqUqAY+MOvA67nnpdawvkHgJBFVPnxui45XH4BwTwbtDucx+Mo7EK4mS0Ti+P" +
                    "1NzARxFNCUFM8Wxc32wxXKff6WU4TbqUx/UJm485ttkFqu0Ox4wTUUbn0uuzK7yV" +
                    "3Y986EtGzhKBraMH36MekSYlE473GqHetRi9qbNG5pM++Sa+WjR9E1e0Yws16CGq" +
                    "smVKwAqg4uc43eBTFUhVAgMBAAGjggEpMIIBJTAOBgNVHQ8BAf8EBAMCAQYwEgYD" +
                    "VR0TAQH/BAgwBgEB/wIBADAdBgNVHQ4EFgQU3bPnbagu6MVObs905nU8lBXO6B0w" +
                    "HwYDVR0jBBgwFoAUj/BLf6guRSSuTVD6Y5qL3uLdG7wwPgYIKwYBBQUHAQEEMjAw" +
                    "MC4GCCsGAQUFBzABhiJodHRwOi8vb2NzcDIuZ2xvYmFsc2lnbi5jb20vcm9vdHIz" +
                    "MDYGA1UdHwQvMC0wK6ApoCeGJWh0dHA6Ly9jcmwuZ2xvYmFsc2lnbi5jb20vcm9v" +
                    "dC1yMy5jcmwwRwYDVR0gBEAwPjA8BgRVHSAAMDQwMgYIKwYBBQUHAgEWJmh0dHBz" +
                    "Oi8vd3d3Lmdsb2JhbHNpZ24uY29tL3JlcG9zaXRvcnkvMA0GCSqGSIb3DQEBCwUA" +
                    "A4IBAQBVaJzl0J/i0zUV38iMXIQ+Q/yht+JZZ5DW1otGL5OYV0LZ6ZE6xh+WuvWJ" +
                    "J4hrDbhfo6khUEaFtRUnurqzutvVyWgW8msnoP0gtMZO11cwPUMUuUV8iGyIOuIB" +
                    "0flo6G+XbV74SZuR5v5RAgqgGXucYUPZWvv9AfzMMQhRQkr/MO/WR2XSdiBrXHoD" +
                    "L2xk4DmjA4K6iPI+1+qMhyrkUM/2ZEdA8ldqwl8nQDkKS7vq6sUZ5LPVdfpxJZZu" +
                    "5JBj4y7FNFTVW1OMlCUvwt5H8aFgBMLFik9xqK6JFHpYxYmf4t2sLLxN0LlCthJE" +
                    "abvp10ZlOtfu8hL5gCXcxnwGxzSb"
//        //trusted chain from BVB Production - update production value
        val bvbProdR =
            "MIIDXzCCAkegAwIBAgILBAAAAAABIVhTCKIwDQYJKoZIhvcNAQELBQAwTDEgMB4G" +
                    "A1UECxMXR2xvYmFsU2lnbiBSb290IENBIC0gUjMxEzARBgNVBAoTCkdsb2JhbFNp" +
                    "Z24xEzARBgNVBAMTCkdsb2JhbFNpZ24wHhcNMDkwMzE4MTAwMDAwWhcNMjkwMzE4" +
                    "MTAwMDAwWjBMMSAwHgYDVQQLExdHbG9iYWxTaWduIFJvb3QgQ0EgLSBSMzETMBEG" +
                    "A1UEChMKR2xvYmFsU2lnbjETMBEGA1UEAxMKR2xvYmFsU2lnbjCCASIwDQYJKoZI" +
                    "hvcNAQEBBQADggEPADCCAQoCggEBAMwldpB5BngiFvXAg7aEyiie/QV2EcWtiHL8" +
                    "RgJDx7KKnQRfJMsuS+FggkbhUqsMgUdwbN1k0ev1LKMPgj0MK66X17YUhhB5uzsT" +
                    "gHeMCOFJ0mpiLx9e+pZo34knlTifBtc+ycsmWQ1z3rDI6SYOgxXG71uL0gRgykmm" +
                    "KPZpO/bLyCiR5Z2KYVc3rHQU3HTgOu5yLy6c+9C7v/U9AOEGM+iCK65TpjoWc4zd" +
                    "QQ4gOsC0p6Hpsk+QLjJg6VfLuQSSaGjlOCZgdbKfd/+RFO+uIEn8rUAVSNECMWEZ" +
                    "XriX7613t2Saer9fwRPvm2L7DWzgVGkWqQPabumDk3F2xmmFghcCAwEAAaNCMEAw" +
                    "DgYDVR0PAQH/BAQDAgEGMA8GA1UdEwEB/wQFMAMBAf8wHQYDVR0OBBYEFI/wS3+o" +
                    "LkUkrk1Q+mOai97i3Ru8MA0GCSqGSIb3DQEBCwUAA4IBAQBLQNvAUKr+yAzv95ZU" +
                    "RUm7lgAJQayzE4aGKAczymvmdLm6AC2upArT9fHxD4q/c2dKg8dEe3jgr25sbwMp" +
                    "jjM5RcOO5LlXbKr8EpbsU8Yt5CRsuZRj+9xTaGdWPoO4zzUhw8lo/s7awlOqzJCK" +
                    "6fBdRoyV3XpYKBovHd7NADdBj+1EbddTKJd+82cEHhXXipa0095MJ6RMG3NzdvQX" +
                    "mcIfeg7jLQitChws/zyrVQ4PkX4268NXSb7hLi18YIvDQVETI53O9zJrlAGomecs" +
                    "Mx86OyXShkDOOyyGeMlhLxS67ttVb9+E7gUJTb0o2HLO02JQZR7rkpeDMdmztcpH" +
                    "WD9f"
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