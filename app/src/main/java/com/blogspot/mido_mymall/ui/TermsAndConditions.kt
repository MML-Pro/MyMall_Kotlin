package com.blogspot.abtallaldigital.bloggy.ui

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.view.*
import androidx.core.text.HtmlCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.blogspot.mido_mymall.databinding.FragmentTermsAndConditionsBinding


class TermsAndConditions : Fragment(), MenuProvider {

        private var _binding: FragmentTermsAndConditionsBinding? = null
        private val binding get() = _binding!!

        companion object{
            private const val TAC_STRING =
                "<!DOCTYPE html>\n" +
                        "    <html>\n" +
                        "    <body>\n" +
                        "    <strong>Terms &amp; Conditions</strong> <p>\n" +
                        "                  By downloading or using the app, these terms will\n" +
                        "                  automatically apply to you – you should make sure therefore\n" +
                        "                  that you read them carefully before using the app. You’re not\n" +
                        "                  allowed to copy or modify the app, any part of the app, or\n" +
                        "                  our trademarks in any way. You’re not allowed to attempt to\n" +
                        "                  extract the source code of the app, and you also shouldn’t try\n" +
                        "                  to translate the app into other languages or make derivative\n" +
                        "                  versions. The app itself, and all the trademarks, copyright,\n" +
                        "                  database rights, and other intellectual property rights related\n" +
                        "                  to it, still belong to Mohamed Ahmed.\n" +
                        "                </p> <p>\n" +
                        "                  Mohamed Ahmed is committed to ensuring that the app is\n" +
                        "                  as useful and efficient as possible. For that reason, we\n" +
                        "                  reserve the right to make changes to the app or to charge for\n" +
                        "                  its services, at any time and for any reason. We will never\n" +
                        "                  charge you for the app or its services without making it very\n" +
                        "                  clear to you exactly what you’re paying for.\n" +
                        "                </p> <p>\n" +
                        "                  The My Mall app stores and processes personal data that\n" +
                        "                  you have provided to us, to provide my\n" +
                        "                  Service. It’s your responsibility to keep your phone and\n" +
                        "                  access to the app secure. We therefore recommend that you do\n" +
                        "                  not jailbreak or root your phone, which is the process of\n" +
                        "                  removing software restrictions and limitations imposed by the\n" +
                        "                  official operating system of your device. It could make your\n" +
                        "                  phone vulnerable to malware/viruses/malicious programs,\n" +
                        "                  compromise your phone’s security features and it could mean\n" +
                        "                  that the My Mall app won’t work properly or at all.\n" +
                        "                </p> <div><p>\n" +
                        "                    The app does use third-party services that declare their\n" +
                        "                    Terms and Conditions.\n" +
                        "                  </p> <p>\n" +
                        "                    Link to Terms and Conditions of third-party service\n" +
                        "                    providers used by the app\n" +
                        "                  </p> <ul><li><a href=\"https://policies.google.com/terms\" target=\"_blank\" rel=\"noopener noreferrer\">Google Play Services</a></li><!----><li><a href=\"https://www.google.com/analytics/terms/\" target=\"_blank\" rel=\"noopener noreferrer\">Google Analytics for Firebase</a></li><li><a href=\"https://firebase.google.com/terms/crashlytics\" target=\"_blank\" rel=\"noopener noreferrer\">Firebase Crashlytics</a></li><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----><!----></ul></div> <p>\n" +
                        "                  You should be aware that there are certain things that\n" +
                        "                  Mohamed Ahmed will not take responsibility for. Certain\n" +
                        "                  functions of the app will require the app to have an active\n" +
                        "                  internet connection. The connection can be Wi-Fi or provided\n" +
                        "                  by your mobile network provider, but Mohamed Ahmed\n" +
                        "                  cannot take responsibility for the app not working at full\n" +
                        "                  functionality if you don’t have access to Wi-Fi, and you don’t\n" +
                        "                  have any of your data allowance left.\n" +
                        "                </p> <p></p> <p>\n" +
                        "                  If you’re using the app outside of an area with Wi-Fi, you\n" +
                        "                  should remember that the terms of the agreement with your\n" +
                        "                  mobile network provider will still apply. As a result, you may\n" +
                        "                  be charged by your mobile provider for the cost of data for\n" +
                        "                  the duration of the connection while accessing the app, or\n" +
                        "                  other third-party charges. In using the app, you’re accepting\n" +
                        "                  responsibility for any such charges, including roaming data\n" +
                        "                  charges if you use the app outside of your home territory\n" +
                        "                  (i.e. region or country) without turning off data roaming. If\n" +
                        "                  you are not the bill payer for the device on which you’re\n" +
                        "                  using the app, please be aware that we assume that you have\n" +
                        "                  received permission from the bill payer for using the app.\n" +
                        "                </p> <p>\n" +
                        "                  Along the same lines, Mohamed Ahmed cannot always take\n" +
                        "                  responsibility for the way you use the app i.e. You need to\n" +
                        "                  make sure that your device stays charged – if it runs out of\n" +
                        "                  battery and you can’t turn it on to avail the Service,\n" +
                        "                  Mohamed Ahmed cannot accept responsibility.\n" +
                        "                </p> <p>\n" +
                        "                  With respect to Mohamed Ahmed’s responsibility for your\n" +
                        "                  use of the app, when you’re using the app, it’s important to\n" +
                        "                  bear in mind that although we endeavor to ensure that it is\n" +
                        "                  updated and correct at all times, we do rely on third parties\n" +
                        "                  to provide information to us so that we can make it available\n" +
                        "                  to you. Mohamed Ahmed accepts no liability for any\n" +
                        "                  loss, direct or indirect, you experience as a result of\n" +
                        "                  relying wholly on this functionality of the app.\n" +
                        "                </p> <p>\n" +
                        "                  At some point, we may wish to update the app. The app is\n" +
                        "                  currently available on Android – the requirements for the \n" +
                        "                  system(and for any additional systems we\n" +
                        "                  decide to extend the availability of the app to) may change,\n" +
                        "                  and you’ll need to download the updates if you want to keep\n" +
                        "                  using the app. Mohamed Ahmed does not promise that it\n" +
                        "                  will always update the app so that it is relevant to you\n" +
                        "                  and/or works with the Android version that you have\n" +
                        "                  installed on your device. However, you promise to always\n" +
                        "                  accept updates to the application when offered to you, We may\n" +
                        "                  also wish to stop providing the app, and may terminate use of\n" +
                        "                  it at any time without giving notice of termination to you.\n" +
                        "                  Unless we tell you otherwise, upon any termination, (a) the\n" +
                        "                  rights and licenses granted to you in these terms will end;\n" +
                        "                  (b) you must stop using the app, and (if needed) delete it\n" +
                        "                  from your device.\n" +
                        "                </p> <p><strong>Changes to This Terms and Conditions</strong></p> <p>\n" +
                        "                  I may update our Terms and Conditions\n" +
                        "                  from time to time. Thus, you are advised to review this page\n" +
                        "                  periodically for any changes. I will\n" +
                        "                  notify you of any changes by posting the new Terms and\n" +
                        "                  Conditions on this page.\n" +
                        "                </p> <p>\n" +
                        "                  These terms and conditions are effective as of 2023-09-24\n" +
                        "                </p> <p><strong>Contact Us</strong></p> <p>\n" +
                        "                  If you have any questions or suggestions about my\n" +
                        "                  Terms and Conditions, do not hesitate to contact me\n" +
                        "                  at exceptionalman1991@gmail.com.\n" +
                        "                </p> <p>This Terms and Conditions page was generated by <a href=\"https://app-privacy-policy-generator.nisrulz.com/\" target=\"_blank\" rel=\"noopener noreferrer\">App Privacy Policy Generator</a></p>\n" +
                        "    </body>\n" +
                        "    </html>\n" +
                        "      "

        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View {
            // Inflate the layout for this fragment
            _binding = FragmentTermsAndConditionsBinding.inflate(inflater, container, false)

            val menuHost = requireActivity()
            menuHost.addMenuProvider(
                this,
                viewLifecycleOwner,
                androidx.lifecycle.Lifecycle.State.CREATED
            )

            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            binding.tacTV.movementMethod = LinkMovementMethod.getInstance()

            val html = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                HtmlCompat.fromHtml(TAC_STRING, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                @Suppress("DEPRECATION")
                Html.fromHtml(TAC_STRING)
            } as Spannable

            binding.tacTV.text = html


        }

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        }


        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            if (menuItem.itemId == android.R.id.home) {
                findNavController().navigateUp()
            }
            return super.onContextItemSelected(menuItem)
        }

    }