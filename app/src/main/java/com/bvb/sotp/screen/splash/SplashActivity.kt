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

//        val Securemetric_internal_ROOT_CA =
//            "MIIFazCCA1OgAwIBAgIRAIIQz7DSQONZRGPgu2OCiwAwDQYJKoZIhvcNAQELBQAw" +
//                    "TzELMAkGA1UEBhMCVVMxKTAnBgNVBAoTIEludGVybmV0IFNlY3VyaXR5IFJlc2Vh" +
//                    "cmNoIEdyb3VwMRUwEwYDVQQDEwxJU1JHIFJvb3QgWDEwHhcNMTUwNjA0MTEwNDM4" +
//                    "WhcNMzUwNjA0MTEwNDM4WjBPMQswCQYDVQQGEwJVUzEpMCcGA1UEChMgSW50ZXJu" +
//                    "ZXQgU2VjdXJpdHkgUmVzZWFyY2ggR3JvdXAxFTATBgNVBAMTDElTUkcgUm9vdCBY" +
//                    "MTCCAiIwDQYJKoZIhvcNAQEBBQADggIPADCCAgoCggIBAK3oJHP0FDfzm54rVygc" +
//                    "h77ct984kIxuPOZXoHj3dcKi/vVqbvYATyjb3miGbESTtrFj/RQSa78f0uoxmyF+" +
//                    "0TM8ukj13Xnfs7j/EvEhmkvBioZxaUpmZmyPfjxwv60pIgbz5MDmgK7iS4+3mX6U" +
//                    "A5/TR5d8mUgjU+g4rk8Kb4Mu0UlXjIB0ttov0DiNewNwIRt18jA8+o+u3dpjq+sW" +
//                    "T8KOEUt+zwvo/7V3LvSye0rgTBIlDHCNAymg4VMk7BPZ7hm/ELNKjD+Jo2FR3qyH" +
//                    "B5T0Y3HsLuJvW5iB4YlcNHlsdu87kGJ55tukmi8mxdAQ4Q7e2RCOFvu396j3x+UC" +
//                    "B5iPNgiV5+I3lg02dZ77DnKxHZu8A/lJBdiB3QW0KtZB6awBdpUKD9jf1b0SHzUv" +
//                    "KBds0pjBqAlkd25HN7rOrFleaJ1/ctaJxQZBKT5ZPt0m9STJEadao0xAH0ahmbWn" +
//                    "OlFuhjuefXKnEgV4We0+UXgVCwOPjdAvBbI+e0ocS3MFEvzG6uBQE3xDk3SzynTn" +
//                    "jh8BCNAw1FtxNrQHusEwMFxIt4I7mKZ9YIqioymCzLq9gwQbooMDQaHWBfEbwrbw" +
//                    "qHyGO0aoSCqI3Haadr8faqU9GY/rOPNk3sgrDQoo//fb4hVC1CLQJ13hef4Y53CI" +
//                    "rU7m2Ys6xt0nUW7/vGT1M0NPAgMBAAGjQjBAMA4GA1UdDwEB/wQEAwIBBjAPBgNV" +
//                    "HRMBAf8EBTADAQH/MB0GA1UdDgQWBBR5tFnme7bl5AFzgAiIyBpY9umbbjANBgkq" +
//                    "hkiG9w0BAQsFAAOCAgEAVR9YqbyyqFDQDLHYGmkgJykIrGF1XIpu+ILlaS/V9lZL" +
//                    "ubhzEFnTIZd+50xx+7LSYK05qAvqFyFWhfFQDlnrzuBZ6brJFe+GnY+EgPbk6ZGQ" +
//                    "3BebYhtF8GaV0nxvwuo77x/Py9auJ/GpsMiu/X1+mvoiBOv/2X/qkSsisRcOj/KK" +
//                    "NFtY2PwByVS5uCbMiogziUwthDyC3+6WVwW6LLv3xLfHTjuCvjHIInNzktHCgKQ5" +
//                    "ORAzI4JMPJ+GslWYHb4phowim57iaztXOoJwTdwJx4nLCgdNbOhdjsnvzqvHu7Ur" +
//                    "TkXWStAmzOVyyghqpZXjFaH3pO3JLF+l+/+sKAIuvtd7u+Nxe5AW0wdeRlN8NwdC" +
//                    "jNPElpzVmbUq4JUagEiuTDkHzsxHpFKVK7q4+63SM1N95R1NbdWhscdCb+ZAJzVc" +
//                    "oyi3B43njTOQ5yOf+1CceWxG1bQVs5ZufpsMljq4Ui0/1lvh+wjChP4kqKOJ2qxq" +
//                    "4RgqsahDYVvTH9w7jXbyLeiNdd8XM2w9U/t7y0Ff/9yi0GE44Za4rF2LN9d11TPA" +
//                    "mRGunUHBcnWEvgJBQl9nJEiU0Zsnvgc/ubhPgXRR4Xq37Z0j4r7g1SgEEzwxA57d" +
//                    "emyPxgcYxn/eR44/KJ4EBs+lVDR3veyJm+kXQ99b21/+jh5Xos1AnX5iItreGCc="

        val Securemetric_internal_ROOT_CA =
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

        //trusted chain from 118.70.13.108:3443
//        val Securemetric_internal_SSL_CA =
//            "MIIFFjCCAv6gAwIBAgIRAJErCErPDBinU/bWLiWnX1owDQYJKoZIhvcNAQELBQAw" +
//                    "TzELMAkGA1UEBhMCVVMxKTAnBgNVBAoTIEludGVybmV0IFNlY3VyaXR5IFJlc2Vh" +
//                    "cmNoIEdyb3VwMRUwEwYDVQQDEwxJU1JHIFJvb3QgWDEwHhcNMjAwOTA0MDAwMDAw" +
//                    "WhcNMjUwOTE1MTYwMDAwWjAyMQswCQYDVQQGEwJVUzEWMBQGA1UEChMNTGV0J3Mg" +
//                    "RW5jcnlwdDELMAkGA1UEAxMCUjMwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEK" +
//                    "AoIBAQC7AhUozPaglNMPEuyNVZLD+ILxmaZ6QoinXSaqtSu5xUyxr45r+XXIo9cP" +
//                    "R5QUVTVXjJ6oojkZ9YI8QqlObvU7wy7bjcCwXPNZOOftz2nwWgsbvsCUJCWH+jdx" +
//                    "sxPnHKzhm+/b5DtFUkWWqcFTzjTIUu61ru2P3mBw4qVUq7ZtDpelQDRrK9O8Zutm" +
//                    "NHz6a4uPVymZ+DAXXbpyb/uBxa3Shlg9F8fnCbvxK/eG3MHacV3URuPMrSXBiLxg" +
//                    "Z3Vms/EY96Jc5lP/Ooi2R6X/ExjqmAl3P51T+c8B5fWmcBcUr2Ok/5mzk53cU6cG" +
//                    "/kiFHaFpriV1uxPMUgP17VGhi9sVAgMBAAGjggEIMIIBBDAOBgNVHQ8BAf8EBAMC" +
//                    "AYYwHQYDVR0lBBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMBMBIGA1UdEwEB/wQIMAYB" +
//                    "Af8CAQAwHQYDVR0OBBYEFBQusxe3WFbLrlAJQOYfr52LFMLGMB8GA1UdIwQYMBaA" +
//                    "FHm0WeZ7tuXkAXOACIjIGlj26ZtuMDIGCCsGAQUFBwEBBCYwJDAiBggrBgEFBQcw" +
//                    "AoYWaHR0cDovL3gxLmkubGVuY3Iub3JnLzAnBgNVHR8EIDAeMBygGqAYhhZodHRw" +
//                    "Oi8veDEuYy5sZW5jci5vcmcvMCIGA1UdIAQbMBkwCAYGZ4EMAQIBMA0GCysGAQQB" +
//                    "gt8TAQEBMA0GCSqGSIb3DQEBCwUAA4ICAQCFyk5HPqP3hUSFvNVneLKYY611TR6W" +
//                    "PTNlclQtgaDqw+34IL9fzLdwALduO/ZelN7kIJ+m74uyA+eitRY8kc607TkC53wl" +
//                    "ikfmZW4/RvTZ8M6UK+5UzhK8jCdLuMGYL6KvzXGRSgi3yLgjewQtCPkIVz6D2QQz" +
//                    "CkcheAmCJ8MqyJu5zlzyZMjAvnnAT45tRAxekrsu94sQ4egdRCnbWSDtY7kh+BIm" +
//                    "lJNXoB1lBMEKIq4QDUOXoRgffuDghje1WrG9ML+Hbisq/yFOGwXD9RiX8F6sw6W4" +
//                    "avAuvDszue5L3sz85K+EC4Y/wFVDNvZo4TYXao6Z0f+lQKc0t8DQYzk1OXVu8rp2" +
//                    "yJMC6alLbBfODALZvYH7n7do1AZls4I9d1P4jnkDrQoxB3UqQ9hVl3LEKQ73xF1O" +
//                    "yK5GhDDX8oVfGKF5u+decIsH4YaTw7mP3GFxJSqv3+0lUFJoi5Lc5da149p90Ids" +
//                    "hCExroL1+7mryIkXPeFM5TgO9r0rvZaBFOvV2z0gp35Z0+L4WPlbuEjN/lxPFin+" +
//                    "HlUjr8gRsI3qfJOQFy/9rKIJR0Y/8Omwt/8oTWgy1mdeHmmjk7j1nYsvC9JSQ6Zv" +
//                    "MldlTTKB3zhThV1+XWYp6rjd5JW1zbVWEkLNxE7GJThEUG3szgBVGP7pSWTUTsqX" +
//                    "nLRbwHOoq7hHwg=="

        val Securemetric_internal_SSL_CA =
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

            val bvbUatDomain = cf.generateCertificate(
                ByteArrayInputStream(
                    Base64.decode(
                        bvbUatDomainB64,
                        Base64.DEFAULT
                    )
                )
            )
            keyStore.setCertificateEntry("bvbUatDomain", bvbUatDomain)

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