using UnityEngine;
using System.CollectionsCollections.Generic;

public class LuahubMultiESP : MonoBehaviour
{
    public bool isHeadlockActive = false;
    public bool isEspActive = false;
    public float targetSmoothness = 6.5f; 

    public struct PlayerTarget {
        public Vector3 headPos;
        public Vector3 basePos;
    }

    private List<PlayerTarget> targets = new List<PlayerTarget>();

    void Update()
    {
        if (isHeadlockActive && Camera.main != null) {
            PlayerTarget closeEnemy = FindClosestEnemy();
            if (closeEnemy.headPos != Vector3.zero) {
                Vector3 lookDirection = (closeEnemy.headPos - Camera.main.transform.position).normalized;
                // Mekanisme pergerakan smooth pengunci agar aman dari deteksi ban otomatis server Garena
                Camera.main.transform.forward = Vector3.Lerp(Camera.main.transform.forward, lookDirection, Time.deltaTime * targetSmoothness);
            }
        }
    }

    void OnGUI()
    {
        if (isEspActive && Camera.main != null) {
            foreach (var enemy in targets) {
                Vector3 screenPoint = Camera.main.WorldToScreenPoint(enemy.basePos);
                if (screenPoint.z > 0) {
                    Vector3 screenStart = new Vector3(Screen.width / 2, Screen.height, 0);
                    DrawLine(screenStart, screenPoint, Color.cyan, 2.5f);
                }
            }
        }
    }

    private void DrawLine(Vector3 start, Vector3 end, Color col, float width) {
        Texture2D lineTex = new Texture2D(1, 1);
        lineTex.SetPixel(0, 0, col);
        lineTex.Apply();
        Vector3 delta = end - start;
        float angle = Mathf.Atan2(delta.y, delta.x) * Mathf.Rad2Deg;
        GUIUtility.RotateAroundTransform(angle, start);
        GUI.DrawTexture(new Rect(start.x, start.y, delta.magnitude, width), lineTex);
        GUIUtility.RotateAroundTransform(-angle, start);
    }

    private PlayerTarget FindClosestEnemy() { return new PlayerTarget(); }
}
