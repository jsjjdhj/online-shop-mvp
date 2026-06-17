import os
import hashlib
import traceback

IGNORE_DIRS = {'.git', 'node_modules', 'target', 'dist', '.idea', '.vscode'}

def collect_files(root):
    result = {}
    for dirpath, _, filenames in os.walk(root):
        # skip ignored dirs
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

try:
    cur_root = r'c:\Users\HP\Desktop\软件测试课设'
    other_root = r'c:\Users\HP\Desktop\软件测试课设\未合并版本\online-shop-mvp-master\online-shop-mvp-master'
    cur = collect_files(cur_root)
    other = collect_files(other_root)

    only_in_cur = []
    only_in_other = []
    different = []
    same = []

    for rel in sorted(set(cur.keys()) | set(other.keys())):
        if rel.startswith('未合并版本') or rel.startswith('diff_summary.txt') or rel.startswith('compare_dirs.py') or rel.startswith('compare_error.txt'):
            continue
        if rel not in cur:
            only_in_other.append(rel)
        elif rel not in other:
            only_in_cur.append(rel)
        else:
            if md5(cur[rel]) == md5(other[rel]):
                same.append(rel)
            else:
                different.append(rel)

    out_path = os.path.join(cur_root, 'diff_summary.txt')
    with open(out_path, 'w', encoding='utf-8') as out:
        out.write('=== Only in current project ===\n')
        out.write('\n'.join(only_in_cur) + '\n')
        out.write('\n=== Only in unmerged version ===\n')
        out.write('\n'.join(only_in_other) + '\n')
        out.write('\n=== Different files ===\n')
        out.write('\n'.join(different) + '\n')
        out.write(f'\nCounts: same={len(same)}, different={len(different)}, only_cur={len(only_in_cur)}, only_other={len(only_in_other)}\n')
    print('done', out_path)
except Exception as e:
    with open(r'c:\Users\HP\Desktop\软件测试课设\compare_error.txt', 'w', encoding='utf-8') as f:
        f.write(traceback.format_exc())
    print('error', e)
