# 使用 Miniconda 基础镜像
FROM continuumio/miniconda3:latest

# 设置工作目录
WORKDIR /app

# 复制依赖文件
COPY requirements.txt .

# 创建 Conda 环境并安装依赖
RUN conda create -n 3dgsAIBack python=3.10 -y && \
    echo "conda activate 3dgsAIBack" >> ~/.bashrc && \
    /bin/bash -c "source ~/.bashrc && pip install --no-cache-dir -r requirements.txt"

# 复制应用代码
COPY . .

# 激活环境并运行应用
CMD ["/bin/bash", "-c", "source ~/.bashrc && uvicorn main:app --host 0.0.0.0 --port 8000"]