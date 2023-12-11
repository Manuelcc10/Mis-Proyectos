using System.Collections;
using System.Collections.Generic;
using TMPro;
using UnityEngine;

public class marcador : MonoBehaviour

{
    public TextMeshProUGUI coinText;
    public void UpdateCoinText()
    {
        if (coinText != null)
        {
            coinText.text = $"Cuenta Monedas: {GameManager.Instance.coins}";
        }
    }

// Start is called before the first frame update
void Start()
    {
        if (coinText == null)
        {
            Debug.LogError("Asigna el objeto TextMeshPro a la variable coinText en el Editor.");
        }

        UpdateCoinText();

    }

    // Update is called once per frame
    void Update()
    {
        if (coinText != null)
        {
            coinText.text = $"Monedas: {GameManager.Instance.coins}";
        }
    }
}