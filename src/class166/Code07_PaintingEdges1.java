package class166;

// 边的涂色，java版
// 测试链接 : https://www.luogu.com.cn/problem/CF576E
// 测试链接 : https://codeforces.com/problemset/problem/576/E
// 提交以下的code，提交时请把类名改成"Main"，可以通过所有测试用例

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Code07_PaintingEdges1 {

	public static int MAXN = 500001;
	public static int MAXK = 51;
	public static int MAXT = 10000001;
	public static int n, m, k, q;

	public static int[] u = new int[MAXN];
	public static int[] v = new int[MAXN];
	public static int[] e = new int[MAXN];
	public static int[] c = new int[MAXN];

	public static int[] pos = new int[MAXN];

	public static int[][] father = new int[MAXK][MAXN << 1];
	public static int[][] siz = new int[MAXK][MAXN << 1];
	public static int[][] rollback = new int[MAXN << 1][3];
	public static int opsize = 0;

	public static int[] head = new int[MAXN << 2];
	public static int[] next = new int[MAXT];
	public static int[] qid = new int[MAXT];
	public static int cnt = 0;

	public static boolean[] ans = new boolean[MAXN];

	public static void addEdge(int i, int qi) {
		next[++cnt] = head[i];
		qid[cnt] = qi;
		head[i] = cnt;
	}

	public static int find(int color, int i) {
		while (i != father[color][i]) {
			i = father[color][i];
		}
		return i;
	}

	public static void union(int color, int x, int y) {
		int fx = find(color, x);
		int fy = find(color, y);
		if (siz[color][fx] < siz[color][fy]) {
			int tmp = fx;
			fx = fy;
			fy = tmp;
		}
		father[color][fy] = fx;
		siz[color][fx] += siz[color][fy];
		rollback[++opsize][0] = color;
		rollback[opsize][1] = fx;
		rollback[opsize][2] = fy;
	}

	public static void undo() {
		int color = rollback[opsize][0];
		int fx = rollback[opsize][1];
		int fy = rollback[opsize--][2];
		father[color][fy] = fy;
		siz[color][fx] -= siz[color][fy];
	}

	public static void add(int jobl, int jobr, int jobq, int l, int r, int i) {
		if (jobl <= l && r <= jobr) {
			addEdge(i, jobq);
		} else {
			int mid = (l + r) >> 1;
			if (jobl <= mid) {
				add(jobl, jobr, jobq, l, mid, i << 1);
			}
			if (jobr > mid) {
				add(jobl, jobr, jobq, mid + 1, r, i << 1 | 1);
			}
		}
	}

	public static void dfs(int l, int r, int i) {
		int unionCnt = 0;
		for (int ei = head[i]; ei > 0; ei = next[ei]) {
			int color = c[qid[ei]];
			int x = u[e[qid[ei]]], xn = x + n, fx = find(color, x), fxn = find(color, xn);
			int y = v[e[qid[ei]]], yn = y + n, fy = find(color, y), fyn = find(color, yn);
			if (fx != fyn) {
				union(color, fx, fyn);
				unionCnt++;
			}
			if (fy != fxn) {
				union(color, fy, fxn);
				unionCnt++;
			}
		}
		if (l == r) {
			if (find(c[l], u[e[l]]) == find(c[l], v[e[l]])) {
				ans[l] = false;
				c[l] = pos[e[l]];
			} else {
				ans[l] = true;
				pos[e[l]] = c[l];
			}
		} else {
			int mid = (l + r) >> 1;
			dfs(l, mid, i << 1);
			dfs(mid + 1, r, i << 1 | 1);
		}
		for (int j = 1; j <= unionCnt; j++) {
			undo();
		}
	}

	public static void prepare() {
		for (int color = 1; color <= k; color++) {
			for (int i = 1; i <= n; i++) {
				father[color][i] = i;
				father[color][i + n] = i + n;
				siz[color][i] = 1;
				siz[color][i + n] = 1;
			}
		}
		for (int i = 1; i <= m; i++) {
			pos[i] = q + 1;
		}
		for (int i = q; i >= 1; i--) {
			if (i + 1 <= pos[e[i]] - 1) {
				add(i + 1, pos[e[i]] - 1, i, 1, q, 1);
			}
			pos[e[i]] = i;
		}
		for (int i = 1; i <= m; i++) {
			pos[i] = 0;
		}
	}

	public static void main(String[] args) {
		FastIO io = new FastIO(System.in, System.out);
		n = io.nextInt();
		m = io.nextInt();
		k = io.nextInt();
		q = io.nextInt();
		for (int i = 1; i <= m; i++) {
			u[i] = io.nextInt();
			v[i] = io.nextInt();
		}
		for (int i = 1; i <= q; i++) {
			e[i] = io.nextInt();
			c[i] = io.nextInt();
		}
		prepare();
		dfs(1, q, 1);
		for (int i = 1; i <= q; i++) {
			if (ans[i]) {
				io.write("YES\n");
			} else {
				io.write("NO\n");
			}
		}
		io.flush();
	}

	// 读写工具类
	static class FastIO {
		private final InputStream is;
		private final OutputStream os;
		private final byte[] inbuf = new byte[1 << 16];
		private int lenbuf = 0;
		private int ptrbuf = 0;
		private final StringBuilder outBuf = new StringBuilder();

		public FastIO(InputStream is, OutputStream os) {
			this.is = is;
			this.os = os;
		}

		private int readByte() {
			if (ptrbuf >= lenbuf) {
				ptrbuf = 0;
				try {
					lenbuf = is.read(inbuf);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				if (lenbuf == -1) {
					return -1;
				}
			}
			return inbuf[ptrbuf++] & 0xff;
		}

		private int skip() {
			int b;
			while ((b = readByte()) != -1) {
				if (b > ' ') {
					return b;
				}
			}
			return -1;
		}

		public int nextInt() {
			int b = skip();
			if (b == -1) {
				throw new RuntimeException("No more integers (EOF)");
			}
			boolean negative = false;
			if (b == '-') {
				negative = true;
				b = readByte();
			}
			int val = 0;
			while (b >= '0' && b <= '9') {
				val = val * 10 + (b - '0');
				b = readByte();
			}
			return negative ? -val : val;
		}

		public void write(String s) {
			outBuf.append(s);
		}

		public void writeInt(int x) {
			outBuf.append(x);
		}

		public void writelnInt(int x) {
			outBuf.append(x).append('\n');
		}

		public void flush() {
			try {
				os.write(outBuf.toString().getBytes());
				os.flush();
				outBuf.setLength(0);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
