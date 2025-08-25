import React, { useRef } from 'react';
import { Canvas, useFrame } from '@react-three/fiber';
import { Icosahedron, OrbitControls } from '@react-three/drei';

/**
 * This component represents the 3D object that will be rendered.
 * An Icosahedron is a 20-sided geometric shape.
 */
function Shape() {
  const meshRef = useRef();

  // The useFrame hook allows you to execute code on every rendered frame
  useFrame(() => {
    if (meshRef.current) {
      // Slowly rotate the shape
      meshRef.current.rotation.x += 0.001;
      meshRef.current.rotation.y += 0.002;
    }
  });

  return (
    <Icosahedron ref={meshRef} args={[2, 1]}>
      {/* The material defines the appearance of the object */}
      <meshStandardMaterial 
        color="#282c34" 
        wireframe={true} // Renders the shape as a wireframe
        emissive="#61dafb" // Gives it a glowing effect, using your app's link color
        emissiveIntensity={0.5}
      />
    </Icosahedron>
  );
}

/**
 * This is the main 3D scene component. It sets up the canvas, lighting,
 * and includes the shape.
 */
function Scene() {
  return (
    <Canvas style={{ position: 'absolute', top: 0, left: 0, zIndex: 0 }}>
      {/* Ambient light provides a soft, even light across the scene */}
      <ambientLight intensity={0.2} />
      {/* Directional light acts like a sun, casting light from a specific angle */}
      <directionalLight position={[10, 10, 5]} intensity={1} />
      
      <Shape />

      {/* OrbitControls allows you to interact with the shape using your mouse (optional but cool) */}
      <OrbitControls 
        enableZoom={false} 
        enablePan={false}
        autoRotate={true}
        autoRotateSpeed={0.5}
      />
    </Canvas>
  );
}

export default Scene;