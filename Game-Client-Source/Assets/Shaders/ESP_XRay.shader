Shader "LUAHUB/ESP_XRay"
{
    Properties
    {
        _XRayColor ("X-Ray Wallhack Color", Color) = (1, 0, 0, 1) // Default Merah Tembus Pandang
        _MainTex ("Base Texture (RGB)", 2D) = "white" {}
    }
    SubShader
    {
        Tags { "Queue"="Overlay" "RenderType"="Opaque" }
        LOD 100

        Pass
        {
            // Trik utama Wallhack: ZTest Always merender objek meskipun tertutup tembok
            ZTest Always
            ZWrite Off
            Blend SrcAlpha OneMinusSrcAlpha

            CGPROGRAM
            #pragma vertex vert
            #pragma fragment frag
            #include "UnityCG.cginc"

            fixed4 _XRayColor;

            struct appdata {
                float4 vertex : POSITION;
            };

            struct v2f {
                float4 vertex : SV_POSITION;
            };

            v2f vert (appdata v) {
                v2f o;
                o.vertex = UnityObjectToClipPos(v.vertex);
                return o;
            }

            fixed4 frag (v2f i) : SV_Target {
                return _XRayColor; // Mengubah karakter musuh menjadi warna solid bersinar di balik tembok
            }
            ENDCG
        }
    }
}
