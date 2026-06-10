// utils/threejsManager.js
import * as THREE from 'three';
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls';
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader';
import { PLYLoader } from 'three/examples/jsm/loaders/PLYLoader';

export class ThreeJSManager {
    constructor(containerId) {
        this.containerId = containerId;
        this.container = null;
        this.scene = null;
        this.camera = null;
        this.renderer = null;
        this.controls = null;
        this.gltfLoader = null;
		this.plyLoader = null;
        this.currentModel = null;
        this.animationId = null;
        this.isInitialized = false;
    }

    // 初始化 Three.js
    init() {
        return new Promise((resolve, reject) => {
            try {
                // 获取容器
                this.container = document.getElementById(this.containerId);
                if (!this.container) {
                    throw new Error(`找不到容器: ${this.containerId}`);
                }

                // 创建场景
                this.scene = new THREE.Scene();
                this.scene.background = new THREE.Color(0x1a1a1a);

                // 创建相机
                this.camera = new THREE.PerspectiveCamera(
                    75,
                    this.container.clientWidth / this.container.clientHeight,
                    0.1,
                    1000
                );
                this.camera.position.set(2, 2, 5);

                // 创建渲染器
                this.renderer = new THREE.WebGLRenderer({
                    antialias: true,
                    alpha: true
                });
                this.renderer.setSize(this.container.clientWidth, this.container.clientHeight);
                this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
                this.container.appendChild(this.renderer.domElement);

                // 添加灯光
                const ambientLight = new THREE.AmbientLight(0x404040, 2);
                this.scene.add(ambientLight);

                const directionalLight = new THREE.DirectionalLight(0xffffff, 1);
                directionalLight.position.set(5, 5, 5);
                this.scene.add(directionalLight);

                // 添加轨道控制器
                this.controls = new OrbitControls(this.camera, this.renderer.domElement);
                this.controls.enableDamping = true;
                this.controls.dampingFactor = 0.05;

                // 初始化 GLTF 加载器
                this.gltfLoader = new GLTFLoader();
				// 初始化 PLY 加载器
                this.plyLoader = new PLYLoader(); 
				
                this.isInitialized = true;
                console.log('Three.js 初始化完成');

                // 开始动画循环
                this.animate();

                resolve(this);
            } catch (error) {
                console.error('Three.js 初始化失败:', error);
                reject(error);
            }
        });
    }

    // 加载 3D 模型
    loadModel(modelUrl, onProgress = null, onError = null) {
        return new Promise((resolve, reject) => {
            if (!this.isInitialized) {
                const error = new Error('Three.js 未初始化');
                if (onError) onError(error);
                reject(error);
                return;
            }

            // 清理现有模型
            if (this.currentModel) {
                this.scene.remove(this.currentModel);
                this.cleanupModel(this.currentModel);
            }

            // 根据文件扩展名选择加载器
            const fileExtension = modelUrl.split('.').pop().toLowerCase();
            
            if (fileExtension === 'ply') {
                this.loadPLYModel(modelUrl, onProgress, onError, resolve, reject);
            } else if (['glb', 'gltf', 'fbx', 'obj'].includes(fileExtension)) {
                this.loadGLTFModel(modelUrl, onProgress, onError, resolve, reject);
            } else {
                const error = new Error(`不支持的模型格式: ${fileExtension}`);
                if (onError) onError(error);
                reject(error);
            }
        });
    }

    // 加载 GLTF/GLB 模型
    loadGLTFModel(modelUrl, onProgress, onError, resolve, reject) {
        if (!this.gltfLoader) {
            const error = new Error('GLTFLoader 未初始化');
            if (onError) onError(error);
            reject(error);
            return;
        }

        this.gltfLoader.load(
            modelUrl,
            (gltf) => {
                console.log('GLTF 模型加载成功:', gltf);
                this.currentModel = gltf.scene;
                this.scene.add(gltf.scene);
                this.fitCameraToObject(gltf.scene);
                resolve(gltf);
            },
            (xhr) => {
                const percent = (xhr.loaded / xhr.total) * 100;
                console.log(`GLTF 加载进度: ${percent.toFixed(1)}%`);
                if (onProgress) onProgress(percent);
            },
            (error) => {
                console.error('GLTF 模型加载失败:', error);
                if (onError) onError(error);
                reject(error);
            }
        );
    }

    // 加载 PLY 模型
    loadPLYModel(modelUrl, onProgress, onError, resolve, reject) {
        if (!this.plyLoader) {
            const error = new Error('PLYLoader 未初始化');
            if (onError) onError(error);
            reject(error);
            return;
        }

        this.plyLoader.load(
            modelUrl,
            (geometry) => {
                console.log('PLY 模型加载成功:', geometry);
                
                // 为 PLY 几何体创建材质和网格
                const material = new THREE.PointsMaterial({
                    color: 0xffffff,
                    size: 0.01,
                    vertexColors: true // 使用顶点颜色
                });

                // 如果有顶点颜色，使用顶点着色
                if (geometry.attributes.color) {
                    material.vertexColors = true;
                }

                const points = new THREE.Points(geometry, material);
                this.currentModel = points;
                this.scene.add(points);
                
                this.fitCameraToObject(points);
                resolve({ scene: points, geometry: geometry });
            },
            (xhr) => {
                const percent = (xhr.loaded / xhr.total) * 100;
                console.log(`PLY 加载进度: ${percent.toFixed(1)}%`);
                if (onProgress) onProgress(percent);
            },
            (error) => {
                console.error('PLY 模型加载失败:', error);
                if (onError) onError(error);
                reject(error);
            }
        );
    }

    // 自动调整相机适应模型
    fitCameraToObject(object) {
        const box = new THREE.Box3().setFromObject(object);
        const size = box.getSize(new THREE.Vector3());
        const center = box.getCenter(new THREE.Vector3());

        const maxSize = Math.max(size.x, size.y, size.z);
        const fitHeightDistance = maxSize / (2 * Math.atan(Math.PI * this.camera.fov / 360));
        const fitWidthDistance = fitHeightDistance / this.camera.aspect;
        let distance = 1.5 * Math.max(fitHeightDistance, fitWidthDistance);

        // 对于点云，调整距离系数
        if (object.isPoints) {
            distance *= 1.5; // 点云需要更远的视角
        }

        const direction = this.controls.target
            .clone()
            .sub(this.camera.position)
            .normalize()
            .multiplyScalar(distance);

        this.controls.maxDistance = distance * 10;
        this.controls.target.copy(center);
        this.camera.position.copy(center).sub(direction);
        this.controls.update();	
    }

    // 动画循环
    animate() {
        this.animationId = requestAnimationFrame(this.animate.bind(this));

        if (this.controls) {
            this.controls.update();
        }

        if (this.renderer && this.scene && this.camera) {
            this.renderer.render(this.scene, this.camera);
        }
    }

    // 清理模型资源
    cleanupModel(model) {
        if (!model) return;

        model.traverse((child) => {
            if (child.isMesh) {
                if (child.geometry) {
                    child.geometry.dispose();
                }
                if (child.material) {
                    if (Array.isArray(child.material)) {
                        child.material.forEach(material => material.dispose());
                    } else {
                        child.material.dispose();
                    }
                }
            }
        });
    }

    // 窗口大小变化处理
    handleResize() {
        if (this.camera && this.renderer && this.container) {
            this.camera.aspect = this.container.clientWidth / this.container.clientHeight;
            this.camera.updateProjectionMatrix();
            this.renderer.setSize(this.container.clientWidth, this.container.clientHeight);
        }
    }

    // 显示测试立方体
    showTestCube() {
        // 清理现有模型
        if (this.currentModel) {
            this.scene.remove(this.currentModel);
        }

        // 创建立方体
        const geometry = new THREE.BoxGeometry(1, 1, 1);
        const material = new THREE.MeshPhongMaterial({
            color: 0x00ff00,
            transparent: true,
            opacity: 0.8
        });
        const cube = new THREE.Mesh(geometry, material);
        this.scene.add(cube);
        this.currentModel = cube;

        // 添加线框
        const edges = new THREE.EdgesGeometry(geometry);
        const line = new THREE.LineSegments(edges, new THREE.LineBasicMaterial({ color: 0xffffff }));
        cube.add(line);

        this.fitCameraToObject(cube);
    }

    // 清理资源
    dispose() {
        // 停止动画循环
        if (this.animationId) {
            cancelAnimationFrame(this.animationId);
        }

        // 清理模型
        if (this.currentModel) {
            this.cleanupModel(this.currentModel);
            this.scene.remove(this.currentModel);
            this.currentModel = null;
        }

        // 清理控制器
        if (this.controls) {
            this.controls.dispose();
        }

        // 清理渲染器
        if (this.renderer) {
            if (this.container && this.renderer.domElement) {
                this.container.removeChild(this.renderer.domElement);
            }
            this.renderer.dispose();
        }

        this.isInitialized = false;
        console.log('Three.js 资源已清理');
    }

    // 获取当前状态
    getStatus() {
        return {
            isInitialized: this.isInitialized,
            hasModel: !!this.currentModel,
            container: this.container ? '已找到' : '未找到',
            scene: !!this.scene,
            camera: !!this.camera,
            renderer: !!this.renderer
        };
    }
}

// 导出单例实例（可选）
let threeJSInstance = null;

export const getThreeJSInstance = (containerId = 'three-container') => {
    if (!threeJSInstance) {
        threeJSInstance = new ThreeJSManager(containerId);
    }
    return threeJSInstance;
};

export default ThreeJSManager;