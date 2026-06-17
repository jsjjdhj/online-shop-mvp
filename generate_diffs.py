import os
import difflib
import hashlib

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

different = []
for rel in sorted(set(cur.keys()) | set(other.keys())):
    if rel.startswith('未合并版本') or rel.startswith('diff_summary') or rel.startswith('compare') or rel.startswith('generate_diffs') or rel.startswith('test.txt'):
        continue
    if rel in cur and rel in other and md5(cur[rel]) != md5(other[rel]):
        different.append(rel)

out_dir = os.path.join(cur_root, 'diffs')
os.makedirs(out_dir, exist_ok=True)

summary = []
for rel in different:
    cur_path = cur[rel]
    other_path = other[rel]
    try:
        with open(cur_path, 'r', encoding='utf-8') as f:
            cur_lines = [line.strip() + '\n' for line in f.readlines()]
        with open(other_path, 'r', encoding='utf-8') as f:
            other_lines = [line.strip() + '\n' for line in f.readlines()]
        diff = list(difflib.unified_diff(cur_lines, other_lines, fromfile='current/' + rel, tofile='unmerged/' + rel, lineterm=''))
        if diff:
            diff_file = os.path.join(out_dir, rel.replace('\\', '__').replace('/', '__') + '.diff')
            with open(diff_file, 'w', encoding='utf-8') as f:
                f.writelines(line + '\n' for line in diff)
            summary.append(f'{rel}: {len(diff)} lines')
        else:
            summary.append(f'{rel}: whitespace-only difference')
    except Exception as e:
        summary.append(f'{rel}: ERROR {e}')

with open(os.path.join(cur_root, 'diff_files_summary.txt'), 'w', encoding='utf-8') as f:
    f.write('\n'.join(summary))
print('done', len(different))
