<!-- ecoreal-repo-header: 3dgs-memory-lab -->
<div align="center">

<img src="assets/repo-icon.png" width="160" alt="3DGS Memory Lab icon">

# 3DGS Memory Lab

**R&D lab for turning photos of nostalgic objects into AI-generated 3D exhibits.**

The experimentation ground behind [`yifang-pavilion`](https://github.com/ecoreal/yifang-pavilion)
— 🥇 First Prize, 19th Chinese Collegiate Computing Competition, Central-South Region (2026).

</div>

## The three parts

| Directory | Role | Stack |
| --- | --- | --- |
| [`3-d_object_ai_back/`](3-d_object_ai_back/) | Generation service: photo / text → 3D model via **Hunyuan3D** and **Doubao** APIs, served on `localhost:8000` | Python · Dockerfile |
| [`3-d_object_back/`](3-d_object_back/) | Exhibition & object management backend | Java · Spring (Maven) |
| [`3-d-objiect-studio/`](3-d-objiect-studio/) | Web studio for browsing the generated 3D exhibits | Vue 3 |

## Run the generation service

```bash
cd 3-d_object_ai_back
conda create -n 3dgsAIBack python=3.10 -y && conda activate 3dgsAIBack
pip install -r requirements.txt
cp config.ini.example config.ini   # add your Hunyuan3D / Doubao keys
python server_all.py               # → http://localhost:8000
```

## Status

Archived R&D snapshots, kept for the record — prompts, generation loops and
pipeline experiments frozen the way the award build used them. The polished
competition product lives in [`yifang-pavilion`](https://github.com/ecoreal/yifang-pavilion).
