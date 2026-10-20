# Currency Widget (Android)

A 4x2 Samsung home-screen widget with a selectable base currency, two selectable outputs, and an editable amount. Defaults: KZT → AMD + USD.

## Build without Android Studio
1. Create a free GitHub account at github.com.
2. Create a new **public** repository (e.g. `currency-widget`).
3. Extract this ZIP, then upload its **contents**, including `.github/workflows/android.yml`, to the repository. GitHub's web upload may hide `.github`; ensure it is included. Commit to `main`.
4. Open **Actions → Build Android APK → Run workflow** (or use the automatic push run).
5. After the green build completes, open its run, download `CurrencyWidget-APK` from **Artifacts**, and unzip to get `app-debug.apk`.
6. Transfer APK to your Samsung, open it and approve installing from that source if prompted. Add widget by long-pressing home screen → Widgets → Currency Widget.
7. Open the app and tap **Refresh official rates**. Set the amount and three currencies, then **Save and update widget**.

## Exchange rates
The app reads the Central Bank of Armenia's official ExchangeRatesLatest SOAP API. All rates are AMD per unit of foreign currency; cross-conversion uses `amount × baseRate / targetRate`. Some official rates are quoted for 10/100/1000 units; the app normalizes the `Amount` field. AMD is exactly 1. Currencies missing from the central bank's response show `rates unavailable` rather than fabricated values. Offline cached rates are used if a refresh fails.

## Limitations
Widget tap opens a configuration screen; Android home-screen widgets do not support normal EditText entry. Widget refresh is **manual in v1** (open app and tap Refresh); automatic background updating has not been implemented. Displayed timestamp is fetch time, not the central bank's official rate effective date. This is a debug APK signed with a development key, suitable for personal use; do not publish it as a production app. The project has not been compiled/tested in this environment; GitHub Actions will surface any build errors.
