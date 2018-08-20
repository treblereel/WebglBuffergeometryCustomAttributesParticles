package org.treblereel.client;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.core.client.EntryPoint;
import elemental2.core.JsArray;
import elemental2.core.TypedArray;
import jsinterop.base.Js;
import org.treblereel.gwt.three4g.THREE;
import org.treblereel.gwt.three4g.Three4G;
import org.treblereel.gwt.three4g.cameras.PerspectiveCamera;
import org.treblereel.gwt.three4g.core.BufferAttribute;
import org.treblereel.gwt.three4g.core.BufferGeometry;
import org.treblereel.gwt.three4g.core.bufferattributes.Float32BufferAttribute;
import org.treblereel.gwt.three4g.loaders.TextureLoader;
import org.treblereel.gwt.three4g.materials.ShaderMaterial;
import org.treblereel.gwt.three4g.materials.parameters.ShaderMaterialParameters;
import org.treblereel.gwt.three4g.materials.parameters.Uniforms;
import org.treblereel.gwt.three4g.math.Color;
import org.treblereel.gwt.three4g.objects.Points;
import org.treblereel.gwt.three4g.renderers.WebGLRenderer;
import org.treblereel.gwt.three4g.renderers.parameters.WebGLRendererParameters;
import org.treblereel.gwt.three4g.scenes.Scene;

import java.util.Date;
import java.util.Random;

import static elemental2.dom.DomGlobal.document;
import static elemental2.dom.DomGlobal.window;

public class App implements EntryPoint {


    private int particles = 100000;
    private Random random = new Random();
    private Points particleSystem;
    private BufferGeometry geometry;
    private WebGLRenderer renderer;


    private Scene scene;
    private PerspectiveCamera camera;

    public void onModuleLoad() {
        Three4G.load();

        scene = new Scene();
        camera = new PerspectiveCamera(40, window.innerWidth / window.innerHeight, 1, 10000);
        camera.position.z = 300;

        Uniforms uniforms = new Uniforms();
        uniforms.set("texture", new TextureLoader().load("https://threejs.org/examples/textures/sprites/spark1.png"));

        ShaderMaterialParameters shaderMaterialParameters = new ShaderMaterialParameters();
        shaderMaterialParameters.uniforms = uniforms;

        shaderMaterialParameters.vertexShader = document.getElementById("vertexshader").textContent;
        shaderMaterialParameters.fragmentShader = document.getElementById("fragmentshader").textContent;
        shaderMaterialParameters.blending = THREE.AdditiveBlending;
        shaderMaterialParameters.depthTest = false;
        shaderMaterialParameters.transparent = true;
        shaderMaterialParameters.vertexColors = THREE.VertexColors;

        ShaderMaterial shaderMaterial = new ShaderMaterial(shaderMaterialParameters);

        int radius = 200;
        geometry = new BufferGeometry();
        JsArray<Float> positions = new JsArray<>();
        JsArray<Float> colors = new JsArray<>();
        JsArray<Float> sizes = new JsArray<>();

        Color color = new Color();
        for (float i = 0; i < particles; i++) {

            positions.push((float) ((random.nextDouble() * 2 - 1) * radius));
            positions.push((float) ((random.nextDouble() * 2 - 1) * radius));
            positions.push((float) ((random.nextDouble() * 2 - 1) * radius));
            color.setHSL(i / particles, 1.0f, 0.5f);
            colors.push(color.r, color.g, color.b);
            sizes.push(20f);
        }

        geometry.addAttribute("position", new Float32BufferAttribute(positions, 3));
        geometry.addAttribute("color", new Float32BufferAttribute(colors, 3));
        geometry.addAttribute("size", new Float32BufferAttribute(sizes, 1));
        particleSystem = new Points(geometry, shaderMaterial);
        scene.add(particleSystem);

        WebGLRendererParameters parameters1 = new WebGLRendererParameters();
        parameters1.antialias = true;
        renderer = new WebGLRenderer(parameters1);
        renderer.setSize(window.innerWidth, window.innerHeight);
        document.body.appendChild(renderer.domElement);

        animate();

    }

    private void animate() {
        AnimationScheduler.get().requestAnimationFrame(timestamp -> {
            render();
            animate();
        });
    }

    private void render() {
        double time = new Date().getTime() * 0.005;
        particleSystem.rotation.z = 0.01f * (float) time;

        BufferAttribute float32BufferAttribute = Js.uncheckedCast(geometry.attributes.getProperty("size"));
        TypedArray sizes = float32BufferAttribute.array;
        for (int i = 0; i < particles; i++) {
            sizes.setAt(i, 10 * (1 + Math.sin(0.1 * i + time)));
        }

        float32BufferAttribute.needsUpdate = true;
        renderer.render(scene, camera);
    }
}
