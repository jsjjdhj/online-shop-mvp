import os
import shutil
import hashlib
import json

IGNORE_DIRS = {'.git', 'node_modules', 'target', 'dist', '.idea', '.vscode'}

def collect_files(root):
    result = {}
    for dirpath, _, filenames in os.walk(root):
        parts = set(os.path.relpath(dirpath, root).split(os.sep))
        if parts & IGNORE_DIRS:
            continue
        for f in filenames:
            full = os.path.join(dirpath, f)
            rel = os.path.relpath(full, root)
            result[rel] = full
    return result

def md5(path):
    h = hashlib.md5()
    try:
        with open(path, 'rb') as f:
            h.update(f.read())
        return h.hexdigest()
    except Exception:
        return None

cur_root = r'c:\Users\HP\Desktop\软件测试课设'
other_root = r'c:\Users\HP\Desktop\软件测试课设\未合并版本\online-shop-mvp-master\online-shop-mvp-master'

cur = collect_files(cur_root)
other = collect_files(other_root)

log = []

# Copy files only in other version (T2/T8 additions)
for rel in sorted(other.keys()):
    if rel.startswith('未合并版本'):
        continue
    if rel not in cur:
        src = other[rel]
        dst = os.path.join(cur_root, rel)
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        shutil.copy2(src, dst)
        log.append(f'COPY {rel}')

# Merge package.json: add scripts from other
pkg_cur_path = os.path.join(cur_root, 'shop-frontend', 'package.json')
pkg_other_path = os.path.join(other_root, 'shop-frontend', 'package.json')
with open(pkg_cur_path, 'r', encoding='utf-8') as f:
    pkg_cur = json.load(f)
with open(pkg_other_path, 'r', encoding='utf-8') as f:
    pkg_other = json.load(f)

scripts_added = []
for script_name in ['test:report', 'test:all']:
    if script_name in pkg_other.get('scripts', {}) and script_name not in pkg_cur.get('scripts', {}):
        pkg_cur['scripts'][script_name] = pkg_other['scripts'][script_name]
        scripts_added.append(script_name)

with open(pkg_cur_path, 'w', encoding='utf-8') as f:
    json.dump(pkg_cur, f, indent=2, ensure_ascii=False)
    f.write('\n')
if scripts_added:
    log.append(f'MERGE shop-frontend\\package.json added scripts: {scripts_added}')

# Merge vitest.config.js: add reporters while keeping setupFiles
cfg_cur_path = os.path.join(cur_root, 'shop-frontend', 'vitest.config.js')
cfg_other_path = os.path.join(other_root, 'shop-frontend', 'vitest.config.js')
with open(cfg_cur_path, 'r', encoding='utf-8') as f:
    cfg_cur_text = f.read()
with open(cfg_other_path, 'r', encoding='utf-8') as f:
    cfg_other_text = f.read()

# Add junit reporter block if not present
if 'junit' not in cfg_cur_text:
    cfg_cur_text = cfg_cur_text.rstrip()
    if cfg_cur_text.endswith('}'):
        # Insert reporters before the final test config closing
        cfg_cur_text = cfg_cur_text[:-1] + """    reporters: [
      'verbose',
      ['junit', { outputFile: 'test-results/junit.xml', suiteName: 'shop-frontend-unit-tests' }]
    ],
    outputFile: {
      junit: 'test-results/junit.xml'
    },
  }
})"""
        with open(cfg_cur_path, 'w', encoding='utf-8') as f:
            f.write(cfg_cur_text)
        log.append('MERGE shop-frontend\\vitest.config.js added junit reporter')

# Different files: keep current for T1/T3, except use other's test resources and pom additions
keep_current = {
    '.gitignore',
    r'docs\测试文档\测试报告\T3-单元测试缺失报告.md',
    r'docs\测试文档\测试报告\T3-单元测试计划与报告.md',
    r'shop-backend\pom.xml',  # keep current, verified
    r'shop-backend\logs\shop-backend.log',
    r'shop-backend\src\main\java\com\shop\controller\AdminController.java',
    r'shop-backend\src\main\java\com\shop\entity\User.java',
    r'shop-backend\src\main\java\com\shop\repository\CartRepository.java',
    r'shop-backend\src\main\java\com\shop\service\impl\CartServiceImpl.java',
    r'shop-backend\src\main\java\com\shop\service\impl\OrderServiceImpl.java',
    r'shop-backend\src\main\resources\application.yml',
    # Keep current test files (T1/T3 already merged)
    r'shop-backend\src\test\java\com\shop\common\BusinessExceptionTest.java',
    r'shop-backend\src\test\java\com\shop\common\GlobalExceptionHandlerTest.java',
    r'shop-backend\src\test\java\com\shop\common\ResultTest.java',
    r'shop-backend\src\test\java\com\shop\controller\AdminControllerTest.java',
    r'shop-backend\src\test\java\com\shop\controller\CartControllerTest.java',
    r'shop-backend\src\test\java\com\shop\controller\OrderControllerTest.java',
    r'shop-backend\src\test\java\com\shop\controller\ProductControllerTest.java',
    r'shop-backend\src\test\java\com\shop\controller\UserControllerTest.java',
    r'shop-backend\src\test\java\com\shop\interceptor\JwtInterceptorTest.java',
    r'shop-backend\src\test\java\com\shop\service\CartServiceTest.java',
    r'shop-backend\src\test\java\com\shop\service\OrderServiceTest.java',
    r'shop-backend\src\test\java\com\shop\service\ProductServiceTest.java',
    r'shop-backend\src\test\java\com\shop\service\UserServiceTest.java',
    r'shop-backend\src\test\java\com\shop\util\JwtUtilTest.java',
    r'shop-backend\src\test\java\com\shop\util\PasswordUtilTest.java',
    r'shop-backend\src\test\java\com\shop\util\TestHelper.java',
    r'shop-frontend\package-lock.json',
}

for rel in sorted(set(cur.keys()) & set(other.keys())):
    if rel.startswith('未合并版本'):
        continue
    if md5(cur[rel]) != md5(other[rel]):
        if rel in keep_current:
            log.append(f'KEEP_CURRENT {rel}')
        elif rel == r'shop-frontend\package.json':
            log.append('MERGE shop-frontend\\package.json')
        elif rel == r'shop-frontend\vitest.config.js':
            log.append('MERGE shop-frontend\\vitest.config.js')
        else:
            # default keep current
            log.append(f'KEEP_CURRENT(default) {rel}')

with open(os.path.join(cur_root, 'merge_log.txt'), 'w', encoding='utf-8') as f:
    f.write('\n'.join(log))
print('done', len(log))
