const path = require("path");
const fs = require("fs");
const { execSync } = require("child_process");
const esbuild = require("esbuild");

const projectRoot = path.resolve(__dirname, "..");
const buildDir = path.join(projectRoot, "plugin-build");
const resourcesDir = path.resolve(projectRoot, "..", "backend", "src", "main", "resources", "frontend");
const publicDir = path.join(projectRoot, "public");
const staticAssets = ["switch-logo.png"];

function ensureDir(dir) {
  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }
}

function runTailwind() {
  console.log("[build-plugin] Generating CSS with tailwindcss...");
  ensureDir(buildDir);
  execSync("npx tailwindcss -i ./app/globals.css -o ./plugin-build/hr-portal.css --minify", {
    cwd: projectRoot,
    stdio: "inherit",
  });
}

async function runEsbuild() {
  console.log("[build-plugin] Bundling React app with esbuild...");
  await esbuild.build({
    entryPoints: [path.join(projectRoot, "plugin-entry.tsx")],
    outfile: path.join(buildDir, "hr-portal.js"),
    bundle: true,
    format: "iife",
    platform: "browser",
    sourcemap: false,
    minify: true,
    target: ["es2019"],
    tsconfig: path.join(projectRoot, "tsconfig.json"),
    define: {
      "process.env.NODE_ENV": '"production"',
    },
  });
}

function copyArtifacts() {
  ensureDir(resourcesDir);
  const artifacts = ["hr-portal.css", "hr-portal.js"];
  for (const file of artifacts) {
    const source = path.join(buildDir, file);
    const target = path.join(resourcesDir, file);
    fs.copyFileSync(source, target);
    console.log(`[build-plugin] Copied ${file} -> ${path.relative(projectRoot, target)}`);
  }
}

function copyStaticAssets() {
  if (!fs.existsSync(publicDir)) {
    return;
  }

  ensureDir(resourcesDir);
  for (const assetName of staticAssets) {
    const sourcePath = path.join(publicDir, assetName);
    const targetPath = path.join(resourcesDir, assetName);

    if (!fs.existsSync(sourcePath)) {
      console.warn(`[build-plugin] Static asset ${assetName} not found in public/, skipping.`);
      continue;
    }

    ensureDir(path.dirname(targetPath));
    fs.copyFileSync(sourcePath, targetPath);
    console.log(
      `[build-plugin] Copied asset public/${assetName} -> ${path.relative(projectRoot, targetPath)}`,
    );
  }
}

(async function build() {
  try {
    runTailwind();
    await runEsbuild();
    copyArtifacts();
    copyStaticAssets();
    console.log("[build-plugin] Build completed successfully.");
  } catch (error) {
    console.error("[build-plugin] Build failed:", error);
    process.exitCode = 1;
  }
})();
