package com.bvb.sotp.screen.splash

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import com.bvb.sotp.BuildConfig
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
import java.security.MessageDigest
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * VuNA:
 * In the SplashActivity.kt class, the Model-View-Presenter (MVP) pattern is being used.
 * This is a software architectural pattern that separates the application's logic
 * into three interconnected components:
 * - Model: This represents the data and the business logic of the application.
 *      It's responsible for retrieving and storing data, as well as performing
 *      any necessary data processing. In this case, the AccountRepository class could be
 *      considered part of the Model, as it's used to manage user accounts and authentication.
 * - View: This is responsible for displaying the data provided by the Model
 *      and forwarding user actions to the Presenter. In this context,
 *      SplashActivity acts as the View. It inherits from MvpLoginActivity<SplashPresenterContract>
 *      and implements SplashViewContract,
 *      indicating that it's responsible for rendering the UI and handling user interactions.
 * - Presenter: This acts as a bridge between the Model and the View.
 *      It retrieves data from the Model, applies the UI logic, and updates the View.
 *      In this case, SplashPresenter would be the Presenter
 *      (although it's not directly visible in the provided code).
 *      It's likely responsible for handling the logic related to the splash screen,
 *      such as deciding what screen to navigate to next.
 *
 * In the SplashActivity class, the gotoNext() function is an example of a method
 * that would be called by the Presenter to instruct the View to navigate to the next screen.
 */

/**
 * VuNA:
 * This is the first screen that is shown when the app is launched.
 * It is responsible for initializing the SDK and navigating to the next screen.
 * Here is the sequence of these function calls:
 * 1. onCreate()
 * 2. setupViews()
 * 3. gotoNext()
 */
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

    /**
     * VuNA:
     * This function is part of the Android lifecycle and is the first to be called
     * when an activity is created. In this function, the activity's user interface
     * is usually set up and any necessary initializations are performed.
     */
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

    private fun startCount() {

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
        // 22/11/23 16:36 VuNA: Added this single cert for domain
        val bvbUatDomainB64 =
            "MIIEmzCCA4OgAwIBAgIUHkZwcbcN3lvlrV9PJsvgAjJ4bo0wDQYJKoZIhvcNAQEL" +
                    "BQAwKTEMMAoGA1UEAwwDU1NMMQwwCgYDVQQKDANTU0wxCzAJBgNVBAYTAlZOMB4X" +
                    "DTIxMDcyMjA3MTEyMVoXDTIzMDYxMzE3MjczNVowITEfMB0GA1UEAwwWc29mdG90" +
                    "cC5iYW92aWV0YmFuay52bjCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEB" +
                    "AO0n4j+0zrZ7KZp/fDBO9zTUXX1GkjMEGOZ7Zo412VQdIDBGMIWsxisIqqT/I8vc" +
                    "MhJyJoIoYoX8B94QHFsjZbQYR8W7va7WEYt1+uWuAOkJGkhSQKeTxO7exSY9H8tw" +
                    "Y4g7e+bIKVpzJWRfh0CzaExMtz+d8Io/B+OE7KqN82zeX7VN/X5izv3Q8cVaHnIh" +
                    "/wwKOvIMjf71zH0KtC3BA56a7sUdcKmUBa7iYf/hrCGUElVfzkpqrj5E6OOzDDq+" +
                    "8F6O/eFzrie0h63TJoRAXQR3zBAsWgWxeRlhTiiKkQhZsoG/QqvBbQsLpVfwvCoS" +
                    "HT/UD8tmOrGLiuwjznwqjj0CAwEAAaOCAcEwggG9MAwGA1UdEwEB/wQCMAAwHwYD" +
                    "VR0jBBgwFoAUTAeT4NnpuSRjxrlzx0ND/Fh7HTkwbAYIKwYBBQUHAQEEYDBeMFwG" +
                    "CCsGAQUFBzABhlBodHRwOi8vZWMyLTMtOTMtMTQzLTE5OC5jb21wdXRlLTEuYW1h" +
                    "em9uYXdzLmNvbTo4MDgwL2VqYmNhL3B1YmxpY3dlYi9zdGF0dXMvb2NzcDAhBgNV" +
                    "HREEGjAYghAqLmJhb3ZpZXRiYW5rLnZuhwRnOKggMCMGA1UdJQQcMBoGBFUdJQAG" +
                    "CCsGAQUFBwMCBggrBgEFBQcDATCBpgYDVR0fBIGeMIGbMIGYoGGgX4ZdaHR0cDov" +
                    "L2xvY2FsaG9zdDo4MDgwL2VqYmNhL3B1YmxpY3dlYi93ZWJkaXN0L2NlcnRkaXN0" +
                    "P2NtZD1jcmwmaXNzdWVyPUNOPVRlc3RDQSxPPUFuYVRvbSxDPVNFojOkMTAvMQ8w" +
                    "DQYDVQQDDAZUZXN0Q0ExDzANBgNVBAoMBkFuYVRvbTELMAkGA1UEBhMCU0UwHQYD" +
                    "VR0OBBYEFK7iknMOKDIv/AtNTEvM6M1GcQ9MMA4GA1UdDwEB/wQEAwIFoDANBgkq" +
                    "hkiG9w0BAQsFAAOCAQEALT/9gh/LNkIQYyogGj4moJC069lnPRlbrn7m7Ue6HbUx" +
                    "skz6gR0wJfbE4+qZk7wSFsCkdA+Rh8Fl8PFV+Pt7JJAuo5LgABe3ukW3gDuPV8yb" +
                    "V/xZHohdZpSO2n6U2fCbmQKl9YcKEtt21W3P+VKuUU15BFZnKJYpALMEMIMzLuvR" +
                    "asEYUMMRBHP0FKvZNfzM0m6055lrhcJYW2gBeisH2z6hB3hCxSF1GeAMhAxi72or" +
                    "Uk60CQ6Vr1xJkodAdWrpWC/+POSKmoDnttnicln+B8QJXIOuaaeHOAxyZreT4f+n" +
                    "vr0WHeIxE1MMc1tjSk+nOXaaTr7AYx13wABqllzacQ=="

        // List of trusted certificates for pinning
        val certificatePinningMap = HashMap<String, String>()

        // Alias: SM-Dev-RootCA
        // Subject DN: CN=Securemetric Vietnam,ST=Hanoi,C=VN
        // Issuer DN:  CN=Securemetric Vietnam,ST=Hanoi,C=VN
        // Subject Alternative Names: null
        // Serial Number (Hex): 60cab578
        // Fingerprint SHA-1:   885EEB8BA129E5C9028BB82F632DC9D8C56A5265
        // Fingerprint SHA-256: 582B0673AFB703C04B212A1047F52B80D6ED0DC163B952509FA7335E933395E7
        // Not Before: Thu Jun 17 09:37:44 GMT+07:00 2021
        // Not After:  Wed Jun 17 09:37:44 GMT+07:00 2026
        certificatePinningMap["SM-Dev-RootCA"] =
            "MIIC9DCCAdygAwIBAgIEYMq1eDANBgkqhkiG9w0BAQsFADA8MQswCQYDVQQGEwJW" +
                    "TjEOMAwGA1UECAwFSGFub2kxHTAbBgNVBAMMFFNlY3VyZW1ldHJpYyBWaWV0bmFt" +
                    "MB4XDTIxMDYxNzAyMzc0NFoXDTI2MDYxNzAyMzc0NFowPDELMAkGA1UEBhMCVk4x" +
                    "DjAMBgNVBAgMBUhhbm9pMR0wGwYDVQQDDBRTZWN1cmVtZXRyaWMgVmlldG5hbTCC" +
                    "ASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAN0PbNrxaUEMfvDeVaLtZCGH" +
                    "0oQxlpuP0nVJ4ZPIS45SYKKNGq4ozWB0LcYR1Ccl3aVSzm9LAm6jpdRajUb3+olD" +
                    "QXUbnTxEaQ5zH8hVlGxcT9ENrmHIHIEGAU4KQeWhGdRxnkCIV5TPKz6eqGRdu0cv" +
                    "uvZQP9l3vAes4wkr/kqYD7rE57vcUdKwvHkoQ2F92E//g1yjbJn3amctxP0fpXTe" +
                    "Pzf7rxELbPPUohD0qjVANk4Xc76bqYmJx5Hofshm6MZ1Kwz3kndPEFvedtnjrIIs" +
                    "83K8w2MSil5jQWCRfd7xmwMeLDkWkGtCM4PRIEHZG74P3mosaspAn9TQ3wbIQJcC" +
                    "AwEAATANBgkqhkiG9w0BAQsFAAOCAQEArsGu1IOOYrh+GFXfSb/YB/VcS5SMVmwU" +
                    "XI+Eqlv/31R6qtE/gqPR0MhtanT3bnASjq/gZzcoLd131qQoyqRk1AMdQcdONwFw" +
                    "hjXt1/4U/PIsh8DcT1VpTpvY5O+8cRWVXehOVDtvfOj+HsMedNDvKkZgidgtvYKy" +
                    "mzNszyN0ojHN1ePl9xmlr1wzcLIUJIN9w0gvfUfocRsH0aJmge0Ka/G8A3RZ/qy2" +
                    "VaumoZZF7HopelWKoOJODloSOstqRcOwzUXuKNJxhOhhsqch6RyDzNCs/YPrqKFt" +
                    "uz68xSAc3wY88o3N+P/Va7uoxyPVIqLer19gxcFBsPEcLECwNwhQ+A=="

        // Alias: SM-Dev-IntermediateCA
        // Subject DN: CN=Securemetric Vietnam ICA,O=SMVN,L=88 Lang Ha Street,ST=Hanoi,C=VN
        // Issuer DN:  CN=Securemetric Vietnam,ST=Hanoi,C=VN
        // Subject Alternative Names: null
        // Serial Number (Hex): 60e4107b
        // Fingerprint SHA-1:   B0D07E9A58D759EE4ECEC2651706BB377C673981
        // Fingerprint SHA-256: 0E386DDFA0A3DBAF484A3C1541697BC692836679539D3CBE273A54E6F0DDA4AE
        // Not Before: Tue Jul 06 15:12:43 GMT+07:00 2021
        // Not After:  Sun Jul 06 15:12:43 GMT+07:00 2025
        certificatePinningMap["SM-Dev-IntermediateCA"] =
            "MIIDQjCCAiqgAwIBAgIEYOQQezANBgkqhkiG9w0BAQsFADA8MQswCQYDVQQGEwJW" +
                    "TjEOMAwGA1UECAwFSGFub2kxHTAbBgNVBAMMFFNlY3VyZW1ldHJpYyBWaWV0bmFt" +
                    "MB4XDTIxMDcwNjA4MTI0M1oXDTI1MDcwNjA4MTI0M1owazELMAkGA1UEBhMCVk4x" +
                    "DjAMBgNVBAgMBUhhbm9pMRowGAYDVQQHDBE4OCBMYW5nIEhhIFN0cmVldDENMAsG" +
                    "A1UECgwEU01WTjEhMB8GA1UEAwwYU2VjdXJlbWV0cmljIFZpZXRuYW0gSUNBMIIB" +
                    "IjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA7z4sobsKZ0hqIcAa5aymzw/G" +
                    "DoRf2JXeeA4dsslo+rXnBdsZhTZbRV6pDnxW1nD1vrykrxgCccYzm5vr07QqxbHr" +
                    "kwIfjJAWUWOGzv/YzVMKmDFdcU9xUb1TljsywicsECbs69cGuTVS5+v3b+4QqONq" +
                    "/rZWpGmNKSatVudR8VkbQDBc0vu/g9zdCc3keMMQRsZeQ3JBkxq7SmMIShgPuPjF" +
                    "bLOVRHPgWT4rThoEDa2ds3kwVlB4ebEdVDVcBEkj0ZQ08orlnbhdGYbTdx3IYGI0" +
                    "M51OFW1YjEJ+zUCiQlTyV07QiBgCS2VZGBwGfI5oCy7HWn+hBWfz4vUQUVHYsQID" +
                    "AQABox0wGzAMBgNVHRMEBTADAQH/MAsGA1UdDwQEAwIBpjANBgkqhkiG9w0BAQsF" +
                    "AAOCAQEAwdI3JXKKUVlPy+HDA//r9JwM5HaLl0LEZRP8YmeuxwOvdJCn2SDHM4pN" +
                    "eJpKJGWNf6xhlbTcU+rBvME4zKs1Ug0t0qvCqIJsrY16GZKo9f3oK+TicnfQq8+q" +
                    "wWv55kcUWiHeKFdB9p016xpHds0bfVog2N+7VYCp+9sK13AtSbqD/cuGw6fHOxuv" +
                    "bNyDtaMZblpytd1D2nZ5dJQUXgaEuuFx8LoYGWnWOR5oKr3RTCGEaq0lm0k/XP8Q" +
                    "vKlHJ3LQc+DszJUxPtxnirtoFkGJvBszoMSAHGEyhZzN3CAsiHLXUhCGY+d9wAVB" +
                    "vJo/4yirVwYw2Msw5yZ5KFUerPLN/A=="

        // Alias: BVB-Production-RootCA
        // Subject DN: CN=GlobalSign,O=GlobalSign,OU=GlobalSign Root CA - R3
        // Issuer DN:  CN=GlobalSign,O=GlobalSign,OU=GlobalSign Root CA - R3
        // Subject Alternative Names: null
        // Serial Number (Hex): 4000000000121585308a2
        // Fingerprint SHA-1:   D69B561148F01C77C54578C10926DF5B856976AD
        // Fingerprint SHA-256: CBB522D7B7F127AD6A0113865BDF1CD4102E7D0759AF635A7CF4720DC963C53B
        // Not Before: Wed Mar 18 17:00:00 GMT+07:00 2009
        // Not After:  Sun Mar 18 17:00:00 GMT+07:00 2029
        certificatePinningMap["BVB-Production-RootCA"] =
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

        // Alias: BVB-Production-IntermediateCA
        // Subject DN: CN=GlobalSign Extended Validation CA - SHA256 - G3,O=GlobalSign nv-sa,C=BE
        // Issuer DN:  CN=GlobalSign,O=GlobalSign,OU=GlobalSign Root CA - R3
        // Subject Alternative Names: null
        // Serial Number (Hex): 48a402dd27920da208349dd1997b
        // Fingerprint SHA-1:   6023192FE7B59D2789130A9FE4094F9B5570D4A2
        // Fingerprint SHA-256: AED5DD9A5339685DFB029F6D89A14335A96512C3CACC52B2994AF8B6B37FA4D2
        // Not Before: Wed Sep 21 07:00:00 GMT+07:00 2016
        // Not After:  Mon Sep 21 07:00:00 GMT+07:00 2026
        certificatePinningMap["BVB-Production-IntermediateCA"] =
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

        // tuanle: KeyStore is a class in Java for storing private keys, certificates, and other security information. KeyStore can be used to encrypt, decrypt, authenticate, and protect data.
        var connectionKeyStore: KeyStore? = null
        try {
            // tuanle:The KeyStore.getInstance function is a function in Java to get a KeyStore object of the specified keystore type
            connectionKeyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            // tuanle: The KeyStore.getInstance function returns a KeyStore object of the corresponding keystore type. To access the KeyStore object, you need to call the load method to create an empty keystore or read from a data source.
            connectionKeyStore!!.load(null, null)
            // tuanle: CertificateFactory.getInstance is a method of the CertificateFactory class in Java, used to create a CertificateFactory object with the specified certificate type. CertificateFactory is a class used to generate certificate objects, certificate paths (CertPaths) and certificate revocation lists (CRLs) from their encodings.
            val cf = CertificateFactory.getInstance("X.509")

            for ((alias, certificateB64) in certificatePinningMap) {
                val certificateObject = cf.generateCertificate(
                    ByteArrayInputStream(
                        Base64.decode(
                            certificateB64,
                            Base64.DEFAULT
                        )
                    )
                )
                connectionKeyStore.setCertificateEntry(
                    alias,
                    certificateObject
                )

                if (BuildConfig.DEBUG) {
                    // Assuming certificateObject is of type X509Certificate
                    val x509Certificate = certificateObject as X509Certificate

                    // Get Subject DN
                    val subjectDn = x509Certificate.subjectX500Principal.name

                    // Get Issuer DN
                    val issuerDn = x509Certificate.issuerX500Principal.name

                    // Get Subject Alternative Names
                    val subjectAlternativeNames = x509Certificate.subjectAlternativeNames

                    // Get serial number
                    val serialNumber = x509Certificate.serialNumber
                    // Convert to Hexadecimal
                    val serialNumberHex = serialNumber.toString(16)

                    // Get fingerprint using SHA-1
                    val mdSHA1 = MessageDigest.getInstance("SHA-1")
                    val derSHA1 = x509Certificate.encoded
                    mdSHA1.update(derSHA1)
                    val digestSHA1 = mdSHA1.digest()
                    val hexStringSHA1 = StringBuilder()
                    for (byte in digestSHA1) {
                        hexStringSHA1.append(String.format("%02X", byte))
                    }
                    val fingerprintSHA1 = hexStringSHA1.toString()

                    // Get fingerprint using SHA-256
                    val mdSHA256 = MessageDigest.getInstance("SHA-256")
                    val derSHA256 = x509Certificate.encoded
                    mdSHA256.update(derSHA256)
                    val digestSHA256 = mdSHA256.digest()
                    val hexStringSHA256 = StringBuilder()
                    for (byte in digestSHA256) {
                        hexStringSHA256.append(String.format("%02X", byte))
                    }
                    val fingerprintSHA256 = hexStringSHA256.toString()

                    // Get notBefore and notAfter dates
                    val notBefore = x509Certificate.notBefore
                    val notAfter = x509Certificate.notAfter

                    // Get Issuer key identifier
                    // Note: There's no direct way to get issuer key identifier from X509Certificate in Java.
                    // You would need to use BouncyCastle or another library to parse the certificate extensions.
                    // That's why we will skip that part for now.

                    Log.d("Certificate", "-------------------------")
                    Log.d("Certificate", "Alias: $alias")
                    Log.d("Certificate", "Subject DN: $subjectDn")
                    Log.d("Certificate", "Issuer DN:  $issuerDn")
                    Log.d("Certificate", "Subject Alternative Names: $subjectAlternativeNames")
                    Log.d("Certificate", "Serial Number (Hex): $serialNumberHex")
                    Log.d("Certificate", "Fingerprint SHA-1:   $fingerprintSHA1")
                    Log.d("Certificate", "Fingerprint SHA-256: $fingerprintSHA256")
                    Log.d("Certificate", "Not Before: $notBefore")
                    Log.d("Certificate", "Not After:  $notAfter")
                    Log.d("Certificate", "-------------------------")
                }
            }

            val bvbProdICA = cf.generateCertificate(
                ByteArrayInputStream(
                    Base64.decode(
                        bvbProdI,
                        Base64.DEFAULT
                    )
                )
            )
            connectionKeyStore.setCertificateEntry("go_Daddy_Root_CA", bvbProdICA)

            val bvbProdRCA = cf.generateCertificate(
                ByteArrayInputStream(
                    Base64.decode(
                        bvbProdR,
                        Base64.DEFAULT
                    )
                )
            )
            connectionKeyStore.setCertificateEntry("go_Daddy_Secure_CA", bvbProdRCA)

            val bvbUatDomain = cf.generateCertificate(
                ByteArrayInputStream(
                    Base64.decode(
                        bvbUatDomainB64,
                        Base64.DEFAULT
                    )
                )
            )
            connectionKeyStore.setCertificateEntry("bvbUatDomain", bvbUatDomain)

        } catch (e: Exception) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace()
            }
        }

        // tuanle: configuration.getInstance().connectionKeyStore = keyStore is a command line in Java, used to assign a KeyStore object to the connectionKeyStore property of a Configuration object. The Configuration object is a class used to manage the configuration parameters of an application or a system


        // The KeyStore instance contains the certificates that are trusted to establish a connection with the server.
        // If the server's certificate doesn't match with any certificate in this KeyStore, the connection will be rejected.
        Configuration.getInstance().connectionKeyStore = connectionKeyStore

        // If you want to accept any certificate from the server, do as below
        // Configuration.getInstance().setConnectionKeyStore(null);
        // Some other code to accept all connections was removed after pen-testing

    }


    // tuanle: override function gotoNext()

    /**
     * 22/11/23 16:36 VuNA: Added this function
     * VuNA: In the context of the Model-View-Presenter (MVP) pattern,
     * which seems to be used in this project, the gotoNext() function is likely
     * a navigation method that is called by the Presenter (SplashPresenter)
     * to instruct the View (SplashActivity) to navigate to the next screen
     * or activity in the application.
     */
    override fun gotoNext() {
//tuanle: AccountRepository.getInstance(this).authentication is a command line in Java, used to get the authentication object from the AccountRepository object
        //The AccountRepository object is a class used to manage user accounts in the application
        //The authentication object is a class used to authenticate and authorize users in the application.
        //The getInstance(this) method is a method of the AccountRepository class, used to return a single AccountRepository object according to the singleton design pattern.
        //The singleton design pattern is a design pattern used to ensure that only one instance of a class is created and provide a global access point to it.
        val authentication = AccountRepository.getInstance(this).deviceAuthentication
        val account = AccountRepository.getInstance(this).accountsData
        if (authentication != null && account.value != null && account.value?.size!! > 0) {
            val intent = LoginActivity.newValidateIntent(this)
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