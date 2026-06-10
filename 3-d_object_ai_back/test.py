from tools import Tools
base64_str = Tools.image_to_base64("./feibi.jpg")
with open("tmp.txt", "w") as f:
    f.write(base64_str)