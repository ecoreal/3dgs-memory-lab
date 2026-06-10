搭建方法(在当前目录下打开终端)：
conda create -n 3dgsAIBack python=3.10 -y
conda activate 3dgsAIBack
pip install -r requirements.txt

环境创建完成后，运行
python server_all.py

这将会在localhost:8000开启服务

之后再次开启AI端服务只需运行：

conda activate 3dgsAIBack
python server_all.py

即可
