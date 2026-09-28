const API = "/api";

const state = {
  currentPage: "dashboard",
  zones: [],
  bins: [],
  stock: [],
  orders: [],
  pickLists: [],
  reports: {}
};

const titles = {
  dashboard: ["Dashboard", "Warehouse overview and current activity"],
  zones: ["Zones", "Manage warehouse zones and route priority"],
  bins: ["Bins", "Register bins and monitor capacity"],
  stock: ["Stock Assignment", "Assign incoming stock using item velocity"],
  orders: ["Orders", "Create and manage warehouse orders"],
  picklists: ["Pick Lists", "Generate and complete optimized picking routes"],
  reports: ["Reports", "Warehouse utilization and velocity analytics"]
};

document.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".nav-item").forEach(btn => {
    btn.addEventListener("click", () => showPage(btn.dataset.page));
  });

  document.getElementById("mobileMenu").addEventListener("click", () => {
    document.querySelector(".sidebar").classList.toggle("open");
  });

  showPage("dashboard");
  checkBackend();
});

async function api(path, options = {}) {
  const config = {
    headers: { "Content-Type": "application/json" },
    ...options
  };

  if (config.body && typeof config.body !== "string") {
    config.body = JSON.stringify(config.body);
  }

  let response;

  try {
    response = await fetch(API + path, config);
  } catch (error) {
    throw new Error(
      "Cannot connect to Spring Boot. Make sure the backend is running on port 8080."
    );
  }

  const text = await response.text();

  let data = null;

  try {
    data = text ? JSON.parse(text) : null;
  } catch {
    data = text;
  }

  if (!response.ok) {
    const message =
      data?.message ||
      data?.error ||
      text ||
      `Request failed (${response.status})`;

    throw new Error(message);
  }

  return data;
}

function showPage(page) {
  state.currentPage = page;

  document
    .querySelectorAll(".page")
    .forEach(p => p.classList.remove("active-page"));

  document
    .getElementById(`page-${page}`)
    .classList.add("active-page");

  document
    .querySelectorAll(".nav-item")
    .forEach(b =>
      b.classList.toggle("active", b.dataset.page === page)
    );

  document.getElementById("pageTitle").textContent =
    titles[page][0];

  document.getElementById("pageSubtitle").textContent =
    titles[page][1];

  document.querySelector(".sidebar").classList.remove("open");

  const renderers = {
    dashboard: renderDashboard,
    zones: renderZones,
    bins: renderBins,
    stock: renderStock,
    orders: renderOrders,
    picklists: renderPickLists,
    reports: renderReports
  };

  renderers[page]();
}

async function refreshCurrentPage() {
  const fn = {
    dashboard: loadDashboard,
    zones: loadZones,
    bins: loadBins,
    stock: loadStock,
    orders: loadOrders,
    picklists: loadPickLists,
    reports: loadReports
  }[state.currentPage];

  if (fn) {
    await fn();
  }
}

async function checkBackend() {
  try {
    await api("/zones");

    document.getElementById("backendStatus").textContent =
      "Online";

    document.querySelector(".status-dot").style.background =
      "#22c55e";

  } catch {
    document.getElementById("backendStatus").textContent =
      "Offline";

    document.querySelector(".status-dot").style.background =
      "#ef4444";
  }
}

function toast(message, type = "success") {
  const el = document.getElementById("toast");

  el.textContent = message;
  el.className = `toast show ${type}`;

  setTimeout(() => {
    el.className = "toast";
  }, 3000);
}

function escapeHtml(value) {
  return String(value ?? "").replace(
    /[&<>"']/g,
    c => ({
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      '"': "&quot;",
      "'": "&#039;"
    }[c])
  );
}

function errorHtml(message) {
  return `<div class="alert error">${escapeHtml(message)}</div>`;
}

function successHtml(message) {
  return `<div class="alert success">${escapeHtml(message)}</div>`;
}

function safeArray(value) {
  return Array.isArray(value) ? value : [];
}

function statusBadge(value) {
  const v = String(value ?? "").toLowerCase();

  return `
    <span class="badge ${escapeHtml(v)}">
      ${escapeHtml(value || "-")}
    </span>
  `;
}

function velocityBadge(value) {
  const v = String(value ?? "").toLowerCase();

  return `
    <span class="badge ${escapeHtml(v)}">
      ${escapeHtml(value || "-")}
    </span>
  `;
}

function formatDate(value) {
  if (!value) return "-";

  try {
    return new Date(value).toLocaleString();
  } catch {
    return value;
  }
}


/* =========================================================
   DASHBOARD
   ========================================================= */

function renderDashboard() {

  const el = document.getElementById("page-dashboard");

  el.innerHTML = `
    <div class="stats" id="dashStats"></div>

    <div class="grid-2">

      <div class="card" id="dashUtil"></div>

      <div class="card" id="dashVelocity"></div>

    </div>

    <div class="card" id="dashRecent"></div>
  `;

  loadDashboard();
}

async function loadDashboard() {

  try {

    const [
      bins,
      orders,
      velocity,
      utilization
    ] = await Promise.all([
      api("/bins"),
      api("/orders"),
      api("/reports/velocity"),
      api("/reports/bin-utilization")
    ]);

    state.bins = safeArray(bins);
    state.orders = safeArray(orders);

    state.reports.velocity = velocity || {};
    state.reports.utilization = utilization || {};

    const totalBins =
      utilization?.totalBins ??
      state.bins.length;

    const occupied =
      utilization?.totalOccupied ??
      state.bins.reduce(
        (s, b) => s + (b.occupiedQuantity || 0),
        0
      );

    const capacity =
      utilization?.totalCapacity ??
      state.bins.reduce(
        (s, b) => s + (b.capacity || 0),
        0
      );

    const util =
      utilization?.utilizationPercentage ??
      (capacity ? occupied / capacity * 100 : 0);

    const pending =
      state.orders.filter(
        o =>
          String(o.status).toUpperCase() === "PENDING"
      ).length;

    document.getElementById("dashStats").innerHTML = `

      <div class="stat-card">
        <div class="label">TOTAL BINS</div>
        <div class="value">${totalBins}</div>
        <div class="hint">Registered storage bins</div>
      </div>

      <div class="stat-card">
        <div class="label">TOTAL CAPACITY</div>
        <div class="value">${capacity}</div>
        <div class="hint">Maximum units</div>
      </div>

      <div class="stat-card">
        <div class="label">OCCUPIED STOCK</div>
        <div class="value">${occupied}</div>
        <div class="hint">Current units in bins</div>
      </div>

      <div class="stat-card">
        <div class="label">PENDING ORDERS</div>
        <div class="value">${pending}</div>
        <div class="hint">Orders waiting for picking</div>
      </div>

    `;

    document.getElementById("dashUtil").innerHTML = `

      <div class="card-header">

        <div>
          <h3>Bin Utilization</h3>
          <p>Current warehouse capacity usage</p>
        </div>

        <strong>
          ${Number(util).toFixed(2)}%
        </strong>

      </div>

      <div class="progress">
        <div
          style="width:${Math.min(
            100,
            Math.max(0, Number(util))
          )}%">
        </div>
      </div>

      <p class="muted">
        ${occupied} occupied /
        ${capacity} total capacity
      </p>

    `;

    document.getElementById("dashVelocity").innerHTML = `

      <div class="card-header">

        <div>
          <h3>Stock Velocity</h3>
          <p>Assigned inventory by velocity</p>
        </div>

      </div>

      <p>
        FAST:
        <strong>${velocity?.fastQuantity ?? 0}</strong>
        units
      </p>

      <p>
        MEDIUM:
        <strong>${velocity?.mediumQuantity ?? 0}</strong>
        units
      </p>

      <p>
        SLOW:
        <strong>${velocity?.slowQuantity ?? 0}</strong>
        units
      </p>

    `;

    const recent =
      state.orders.slice(-5).reverse();

    document.getElementById("dashRecent").innerHTML = `

      <div class="card-header">

        <div>
          <h3>Recent Orders</h3>
          <p>Latest orders from the system</p>
        </div>

      </div>

      ${
        recent.length
          ? `
            <div class="table-wrap">

              <table>

                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Order</th>
                    <th>Status</th>
                    <th>Created</th>
                  </tr>
                </thead>

                <tbody>

                  ${recent
                    .map(
                      o => `
                        <tr>

                          <td>${o.id}</td>

                          <td>
                            ${escapeHtml(o.orderNumber)}
                          </td>

                          <td>
                            ${statusBadge(o.status)}
                          </td>

                          <td>
                            ${formatDate(o.createdAt)}
                          </td>

                        </tr>
                      `
                    )
                    .join("")}

                </tbody>

              </table>

            </div>
          `
          : `<div class="empty">No orders yet.</div>`
      }

    `;

  } catch (e) {

    document.getElementById("dashStats").innerHTML =
      errorHtml(e.message);

  }
}


/* =========================================================
   ZONES
   ========================================================= */

function renderZones() {

  document.getElementById("page-zones").innerHTML = `

    <div class="grid-2">

      <div class="card">

        <div class="card-header">

          <div>
            <h3>Add Zone</h3>
            <p>
              Lower priority means closer to dispatch.
            </p>
          </div>

        </div>

        <form id="zoneForm">

          <div class="form-grid">

            <div class="form-group">

              <label>Zone Name</label>

              <input
                id="zoneName"
                required
                placeholder="Near Dispatch">

            </div>

            <div class="form-group">

              <label>Priority</label>

              <input
                id="zonePriority"
                type="number"
                min="1"
                required
                placeholder="1">

            </div>

          </div>

          <div class="form-actions">

            <button class="primary-btn">
              Save Zone
            </button>

          </div>

        </form>

      </div>

      <div class="card">

        <div class="card-header">

          <div>
            <h3>How Priority Works</h3>
          </div>

        </div>

        <p>
          FAST-moving items prefer lower-priority zones.
          Example: priority 1 is closer to dispatch than priority 3.
        </p>

      </div>

    </div>

    <div class="card">

      <div class="card-header">

        <h3>Zones</h3>

        <button
          class="secondary-btn"
          onclick="loadZones()">
          Refresh
        </button>

      </div>

      <div id="zonesTable"></div>

    </div>

  `;

  document
    .getElementById("zoneForm")
    .addEventListener("submit", createZone);

  loadZones();
}

async function loadZones() {

  try {

    const data = await api("/zones");

    state.zones = safeArray(data);

    document.getElementById("zonesTable").innerHTML =
      state.zones.length
        ? `

          <div class="table-wrap">

            <table>

              <thead>

                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Priority</th>
                </tr>

              </thead>

              <tbody>

                ${state.zones
                  .map(
                    z => `
                      <tr>

                        <td>${z.id}</td>

                        <td>
                          ${escapeHtml(z.name)}
                        </td>

                        <td>
                          ${z.priority}
                        </td>

                      </tr>
                    `
                  )
                  .join("")}

              </tbody>

            </table>

          </div>

        `
        : `<div class="empty">No zones found.</div>`;

  } catch (e) {

    document.getElementById("zonesTable").innerHTML =
      errorHtml(e.message);

  }
}

async function createZone(ev) {

  ev.preventDefault();

  try {

    await api("/zones", {
      method: "POST",
      body: {
        name:
          document
            .getElementById("zoneName")
            .value
            .trim(),

        priority:
          Number(
            document
              .getElementById("zonePriority")
              .value
          )
      }
    });

    ev.target.reset();

    toast("Zone created successfully");

    loadZones();

  } catch (e) {

    toast(e.message, "error");

  }
}


/* =========================================================
   BINS
   ========================================================= */

function renderBins() {

  document.getElementById("page-bins").innerHTML = `

    <div class="card">

      <div class="card-header">

        <div>

          <h3>Register Bin</h3>

          <p>
            Zone must already exist before creating a bin.
          </p>

        </div>

      </div>

      <form id="binForm">

        <div class="form-grid">

          <div class="form-group">

            <label>Bin Code</label>

            <input
              id="binCode"
              required
              placeholder="B101">

          </div>

          <div class="form-group">

            <label>Capacity</label>

            <input
              id="binCapacity"
              type="number"
              min="1"
              required
              placeholder="100">

          </div>

          <div class="form-group">

            <label>Occupied Quantity</label>

            <input
              id="binOccupied"
              type="number"
              min="0"
              value="0"
              required>

          </div>

          <div class="form-group">

            <label>Zone</label>

            <select id="binZone" required>

              <option value="">
                Select zone
              </option>

            </select>

          </div>

        </div>

        <div class="form-actions">

          <button class="primary-btn">
            Save Bin
          </button>

        </div>

      </form>

    </div>

    <div class="card">

      <div class="card-header">

        <h3>Bin Inventory</h3>

        <button
          class="secondary-btn"
          onclick="loadBins()">
          Refresh
        </button>

      </div>

      <div id="binsTable"></div>

    </div>

  `;

  document
    .getElementById("binForm")
    .addEventListener("submit", createBin);

  loadBins();

  loadZoneOptions("binZone");
}

async function loadZoneOptions(id) {

  try {

    const zones = await api("/zones");

    state.zones = safeArray(zones);

    const select =
      document.getElementById(id);

    if (!select) return;

    select.innerHTML =
      `<option value="">Select zone</option>` +
      state.zones
        .map(
          z =>
            `<option value="${z.id}">
              ${escapeHtml(z.name)} (P${z.priority})
            </option>`
        )
        .join("");

  } catch {}
}

async function loadBins() {

  try {

    const data = await api("/bins");

    state.bins = safeArray(data);

    document.getElementById("binsTable").innerHTML =
      state.bins.length
        ? `

          <div class="table-wrap">

            <table>

              <thead>

                <tr>
                  <th>ID</th>
                  <th>Bin</th>
                  <th>Zone</th>
                  <th>Capacity</th>
                  <th>Occupied</th>
                  <th>Available</th>
                  <th>Usage</th>
                </tr>

              </thead>

              <tbody>

                ${state.bins
                  .map(b => {

                    const zone =
                      b.zone || {};

                    const cap =
                      Number(b.capacity || 0);

                    const occ =
                      Number(
                        b.occupiedQuantity || 0
                      );

                    const pct =
                      cap
                        ? Math.min(
                            100,
                            occ / cap * 100
                          )
                        : 0;

                    return `

                      <tr>

                        <td>${b.id}</td>

                        <td>
                          <strong>
                            ${escapeHtml(b.binCode)}
                          </strong>
                        </td>

                        <td>
                          ${escapeHtml(zone.name || "-")}
                        </td>

                        <td>${cap}</td>

                        <td>${occ}</td>

                        <td>${cap - occ}</td>

                        <td style="min-width:120px">

                          <div class="progress">

                            <div
                              style="width:${pct}%">
                            </div>

                          </div>

                          <small>
                            ${pct.toFixed(1)}%
                          </small>

                        </td>

                      </tr>

                    `;

                  })
                  .join("")}

              </tbody>

            </table>

          </div>

        `
        : `<div class="empty">No bins found.</div>`;

  } catch (e) {

    document.getElementById("binsTable").innerHTML =
      errorHtml(e.message);

  }
}

async function createBin(ev) {

  ev.preventDefault();

  const capacity =
    Number(
      document.getElementById("binCapacity").value
    );

  const occupied =
    Number(
      document.getElementById("binOccupied").value
    );

  if (occupied > capacity) {

    return toast(
      "Occupied quantity cannot exceed capacity.",
      "error"
    );

  }

  try {

    await api("/bins", {
      method: "POST",
      body: {

        binCode:
          document
            .getElementById("binCode")
            .value
            .trim(),

        capacity,

        occupiedQuantity:
          occupied,

        zoneId:
          Number(
            document
              .getElementById("binZone")
              .value
          )

      }
    });

    ev.target.reset();

    document.getElementById("binOccupied").value =
      0;

    toast("Bin created successfully");

    loadBins();

  } catch (e) {

    toast(e.message, "error");

  }
}


/* =========================================================
   STOCK
   ========================================================= */

function renderStock() {

  document.getElementById("page-stock").innerHTML = `

    <div class="card">

      <div class="card-header">

        <div>

          <h3>Assign Incoming Stock</h3>

          <p>
            The backend selects a suitable bin based on
            capacity, velocity and zone priority.
          </p>

        </div>

      </div>

      <form id="stockForm">

        <div class="form-grid">

          <div class="form-group">

            <label>Item Code</label>

            <input
              id="stockItem"
              required
              placeholder="ITEM001">

          </div>

          <div class="form-group">

            <label>Quantity</label>

            <input
              id="stockQty"
              type="number"
              min="1"
              required
              placeholder="30">

          </div>

          <div class="form-group">

            <label>Velocity</label>

            <select id="stockVelocity">

              <option>FAST</option>
              <option>MEDIUM</option>
              <option>SLOW</option>

            </select>

          </div>

        </div>

        <div class="form-actions">

          <button class="primary-btn">
            Assign Stock
          </button>

        </div>

      </form>

    </div>

    <div class="card">

      <div class="card-header">

        <h3>Stock Assignments</h3>

        <button
          class="secondary-btn"
          onclick="loadStock()">
          Refresh
        </button>

      </div>

      <div id="stockTable"></div>

    </div>

  `;

  document
    .getElementById("stockForm")
    .addEventListener("submit", createStock);

  loadStock();
}

async function loadStock() {

  try {

    const data =
      await api("/stock-assignments");

    state.stock =
      safeArray(data);

    document.getElementById("stockTable").innerHTML =
      state.stock.length
        ? `

          <div class="table-wrap">

            <table>

              <thead>

                <tr>
                  <th>ID</th>
                  <th>Item</th>
                  <th>Quantity</th>
                  <th>Velocity</th>
                  <th>Bin</th>
                  <th>Zone</th>
                  <th>Assigned</th>
                </tr>

              </thead>

              <tbody>

                ${state.stock
                  .map(s => {

                    const b =
                      s.bin || {};

                    const z =
                      b.zone || {};

                    return `

                      <tr>

                        <td>${s.id}</td>

                        <td>
                          <strong>
                            ${escapeHtml(s.itemCode)}
                          </strong>
                        </td>

                        <td>${s.quantity}</td>

                        <td>
                          ${velocityBadge(s.velocity)}
                        </td>

                        <td>
                          ${escapeHtml(
                            b.binCode || "-"
                          )}
                        </td>

                        <td>
                          ${escapeHtml(
                            z.name || "-"
                          )}
                        </td>

                        <td>
                          ${formatDate(
                            s.assignedAt
                          )}
                        </td>

                      </tr>

                    `;

                  })
                  .join("")}

              </tbody>

            </table>

          </div>

        `
        : `<div class="empty">No stock assignments found.</div>`;

  } catch (e) {

    document.getElementById("stockTable").innerHTML =
      errorHtml(e.message);

  }
}

async function createStock(ev) {

  ev.preventDefault();

  try {

    await api("/stock-assignments", {
      method: "POST",
      body: {

        itemCode:
          document
            .getElementById("stockItem")
            .value
            .trim(),

        quantity:
          Number(
            document
              .getElementById("stockQty")
              .value
          ),

        velocity:
          document
            .getElementById("stockVelocity")
            .value

      }
    });

    ev.target.reset();

    toast("Stock assigned successfully");

    loadStock();
    loadBins();

  } catch (e) {

    toast(e.message, "error");

  }
}


/* =========================================================
   ORDERS
   ========================================================= */

function renderOrders() {

  document.getElementById("page-orders").innerHTML = `

    <div class="card">

      <div class="card-header">

        <div>

          <h3>Create Order</h3>

          <p>
            Add one or more items to an order.
          </p>

        </div>

      </div>

      <form id="orderForm">

        <div class="form-group">

          <label>Order Number</label>

          <input
            id="orderNumber"
            required
            placeholder="ORD001">

        </div>

        <div style="margin-top:15px">

          <label>Order Items</label>

          <div id="orderItems"></div>

        </div>

        <div class="form-actions">

          <button
            type="button"
            class="secondary-btn"
            onclick="addOrderItem()">
            + Add Item
          </button>

          <button class="primary-btn">
            Create Order
          </button>

        </div>

      </form>

    </div>

    <div class="card">

      <div class="card-header">

        <h3>Orders</h3>

        <button
          class="secondary-btn"
          onclick="loadOrders()">
          Refresh
        </button>

      </div>

      <div id="ordersTable"></div>

    </div>

  `;

  addOrderItem();

  document
    .getElementById("orderForm")
    .addEventListener("submit", createOrder);

  loadOrders();
}

function addOrderItem() {

  const box =
    document.getElementById("orderItems");

  const row =
    document.createElement("div");

  row.className =
    "form-grid order-row";

  row.style.marginTop =
    "9px";

  row.innerHTML = `

    <div class="form-group">

      <label>Item Code</label>

      <input
        class="order-item-code"
        required
        placeholder="ITEM001">

    </div>

    <div class="form-group">

      <label>Quantity</label>

      <div style="display:flex;gap:7px">

        <input
          class="order-item-qty"
          type="number"
          min="1"
          required
          placeholder="10">

        <button
          type="button"
          class="danger-btn"
          onclick="this.closest('.order-row').remove()">
          Remove
        </button>

      </div>

    </div>

  `;

  box.appendChild(row);
}

async function loadOrders() {

  try {

    const data =
      await api("/orders");

    state.orders =
      safeArray(data);

    document.getElementById("ordersTable").innerHTML =
      state.orders.length
        ? `

          <div class="table-wrap">

            <table>

              <thead>

                <tr>
                  <th>ID</th>
                  <th>Order</th>
                  <th>Status</th>
                  <th>Created</th>
                  <th>Actions</th>
                </tr>

              </thead>

              <tbody>

                ${state.orders
                  .map(
                    o => `

                      <tr>

                        <td>${o.id}</td>

                        <td>
                          <strong>
                            ${escapeHtml(
                              o.orderNumber
                            )}
                          </strong>
                        </td>

                        <td>
                          ${statusBadge(o.status)}
                        </td>

                        <td>
                          ${formatDate(
                            o.createdAt
                          )}
                        </td>

                        <td>

                          <div class="action-row">

                            <button
                              class="small-btn"
                              onclick="generatePickList(${o.id})">
                              Generate Pick List
                            </button>

                            ${
                              String(
                                o.status
                              ).toUpperCase() !==
                              "COMPLETED"

                                ? `
                                  <button
                                    class="small-btn"
                                    onclick="updateOrderStatus(${o.id}, 'PICKED')">
                                    Mark Picked
                                  </button>
                                `

                                : ""
                            }

                          </div>

                        </td>

                      </tr>

                    `
                  )
                  .join("")}

              </tbody>

            </table>

          </div>

        `
        : `<div class="empty">No orders found.</div>`;

  } catch (e) {

    document.getElementById("ordersTable").innerHTML =
      errorHtml(e.message);

  }
}

async function createOrder(ev) {

  ev.preventDefault();

  const codes =
    [
      ...document.querySelectorAll(
        ".order-item-code"
      )
    ];

  const qtys =
    [
      ...document.querySelectorAll(
        ".order-item-qty"
      )
    ];

  const items =
    codes
      .map(
        (input, i) => ({
          itemCode:
            input.value.trim(),

          quantity:
            Number(
              qtys[i].value
            )
        })
      )
      .filter(
        x =>
          x.itemCode &&
          x.quantity > 0
      );

  if (!items.length) {

    return toast(
      "Add at least one order item.",
      "error"
    );

  }

  try {

    await api("/orders", {
      method: "POST",
      body: {

        orderNumber:
          document
            .getElementById("orderNumber")
            .value
            .trim(),

        items

      }
    });

    ev.target.reset();

    document.getElementById(
      "orderItems"
    ).innerHTML = "";

    addOrderItem();

    toast(
      "Order created successfully"
    );

    loadOrders();

  } catch (e) {

    toast(e.message, "error");

  }
}

async function updateOrderStatus(
  id,
  status
) {

  try {

    await api(
      `/orders/${id}/status?status=${encodeURIComponent(status)}`,
      {
        method: "PUT"
      }
    );

    toast(
      "Order status updated"
    );

    loadOrders();

  } catch (e) {

    toast(
      e.message,
      "error"
    );

  }
}

async function generatePickList(
  orderId
) {

  try {

    await api(
      `/picklists/${orderId}/generate`,
      {
        method: "POST"
      }
    );

    toast(
      "Pick list generated"
    );

    showPage("picklists");

  } catch (e) {

    toast(
      e.message,
      "error"
    );

  }
}


/* =========================================================
   PICK LISTS
   ========================================================= */

function renderPickLists() {

  document.getElementById(
    "page-picklists"
  ).innerHTML = `

    <div class="card">

      <div class="card-header">

        <div>

          <h3>Generate Pick List</h3>

          <p>
            Enter an order number to generate
            the warehouse picking route.
          </p>

        </div>

      </div>

      <div class="form-grid">

        <div class="form-group">

          <label>Order Number</label>

          <input
            id="pickOrderNumber"
            type="text"
            placeholder="ORD001">

        </div>

      </div>

      <div class="form-actions">

        <button
          class="primary-btn"
          onclick="generateFromPage()">
          Generate
        </button>

      </div>

    </div>

    <div class="card">

      <div class="card-header">

        <h3>Find Pick List</h3>

      </div>

      <div class="form-grid">

        <div class="form-group">

          <label>Pick List ID</label>

          <input
            id="pickListId"
            type="number"
            min="1"
            placeholder="1">

        </div>

      </div>

      <div class="form-actions">

        <button
          class="secondary-btn"
          onclick="loadPickListById()">
          View Pick List
        </button>

      </div>

    </div>

    <div id="pickListResult"></div>

  `;

  loadPickLists();
}


/*
 * Generate Pick List using Order Number
 * Example:
 *
 * ORD001
 *   ↓
 * Find order
 *   ↓
 * Get numeric order ID
 *   ↓
 * POST /api/picklists/{orderId}/generate
 */
async function generateFromPage() {

  const orderNumber =
    document
      .getElementById(
        "pickOrderNumber"
      )
      .value
      .trim();

  if (!orderNumber) {

    return toast(
      "Enter an order number.",
      "error"
    );

  }

  try {

    const orders =
      await api("/orders");

    const order =
      safeArray(orders).find(
        o =>
          String(
            o.orderNumber
          ).toLowerCase() ===
          orderNumber.toLowerCase()
      );

    if (!order) {

      return toast(
        `Order ${orderNumber} not found.`,
        "error"
      );

    }

    await generatePickList(
      order.id
    );

  } catch (e) {

    toast(
      e.message,
      "error"
    );

  }
}

async function loadPickLists() {

  /*
   * There is no GET /api/picklists endpoint
   * in the existing controller.
   *
   * We therefore don't guess a list endpoint.
   * Users can retrieve a known pick-list by ID.
   */

  document.getElementById(
    "pickListResult"
  ).innerHTML = `

    <div class="card">

      <div class="empty">

        Enter a Pick List ID above to view it.
        Your current backend exposes
        GET /api/picklists/{id},
        not a list-all endpoint.

      </div>

    </div>

  `;
}

async function loadPickListById() {

  const id =
    Number(
      document
        .getElementById(
          "pickListId"
        )
        .value
    );

  if (!id) {

    return toast(
      "Enter a pick-list ID.",
      "error"
    );

  }

  const result =
    document.getElementById(
      "pickListResult"
    );

  try {

    const p =
      await api(
        `/picklists/${id}`
      );

    const items =
      safeArray(
        p?.items
      );

    result.innerHTML = `

      <div class="card">

        <div class="card-header">

          <div>

            <h3>
              Pick List #${
                p.pickListId ?? id
              }
            </h3>

            <p>
              Order:
              ${escapeHtml(
                p.orderNumber
              )}
            </p>

          </div>

          ${statusBadge(p.status)}

        </div>

        <p class="muted">

          Generated:
          ${formatDate(
            p.generatedAt
          )}

        </p>

        ${
          items.length

            ? `

              <div class="route-list">

                ${items
                  .sort(
                    (a, b) =>
                      (a.routeSequence || 0) -
                      (b.routeSequence || 0)
                  )
                  .map(
                    i => `

                      <div class="route-item">

                        <div class="route-number">
                          ${i.routeSequence}
                        </div>

                        <div>

                          <strong>
                            ${escapeHtml(
                              i.itemCode
                            )}
                          </strong>

                          <br>

                          <small>

                            Bin:
                            ${escapeHtml(
                              i.binCode
                            )}

                            ·

                            Zone:
                            ${escapeHtml(
                              i.zoneName
                            )}

                          </small>

                        </div>

                        <strong>
                          Qty ${i.quantity}
                        </strong>

                      </div>

                    `
                  )
                  .join("")}

              </div>

            `

            : `
              <div class="empty">
                No pick items.
              </div>
            `
        }

        <div class="form-actions">

          <button
            class="primary-btn"
            onclick="completePicking(${id})">
            Pick Stock
          </button>

        </div>

      </div>

    `;

  } catch (e) {

    result.innerHTML =
      errorHtml(e.message);

  }
}

async function completePicking(id) {

  try {

    await api(
      `/picklists/${id}/pick`,
      {
        method: "POST"
      }
    );

    toast(
      "Stock picked and bin occupancy updated"
    );

    loadPickListById();

    loadBins();

  } catch (e) {

    toast(
      e.message,
      "error"
    );

  }
}


/* =========================================================
   REPORTS
   ========================================================= */

function renderReports() {

  document.getElementById(
    "page-reports"
  ).innerHTML = `

    <div class="grid-2">

      <div
        class="card"
        id="utilReport">
      </div>

      <div
        class="card"
        id="velocityReport">
      </div>

    </div>

    <div class="card">

      <div class="card-header">

        <h3>Report Notes</h3>

      </div>

      <p>

        Bin utilization is calculated from
        current bin capacity and occupied quantity.

        Velocity report summarizes stock assignments
        by FAST, MEDIUM and SLOW.

      </p>

    </div>

  `;

  loadReports();
}

async function loadReports() {

  try {

    const [u, v] =
      await Promise.all([
        api("/reports/bin-utilization"),
        api("/reports/velocity")
      ]);

    state.reports.utilization =
      u || {};

    state.reports.velocity =
      v || {};

    document.getElementById(
      "utilReport"
    ).innerHTML = `

      <div class="card-header">

        <div>

          <h3>Bin Utilization</h3>

          <p>
            Warehouse capacity report
          </p>

        </div>

      </div>

      <div
        class="stats"
        style="grid-template-columns:1fr 1fr">

        <div class="stat-card">

          <div class="label">
            TOTAL BINS
          </div>

          <div class="value">
            ${u.totalBins ?? 0}
          </div>

        </div>

        <div class="stat-card">

          <div class="label">
            CAPACITY
          </div>

          <div class="value">
            ${u.totalCapacity ?? 0}
          </div>

        </div>

        <div class="stat-card">

          <div class="label">
            OCCUPIED
          </div>

          <div class="value">
            ${u.totalOccupied ?? 0}
          </div>

        </div>

        <div class="stat-card">

          <div class="label">
            AVAILABLE
          </div>

          <div class="value">
            ${u.availableCapacity ?? 0}
          </div>

        </div>

      </div>

      <h4>
        Utilization:
        ${Number(
          u.utilizationPercentage ?? 0
        ).toFixed(2)}%
      </h4>

      <div class="progress">

        <div
          style="width:${Math.min(
            100,
            Number(
              u.utilizationPercentage || 0
            )
          )}%">
        </div>

      </div>

    `;

    document.getElementById(
      "velocityReport"
    ).innerHTML = `

      <div class="card-header">

        <div>

          <h3>Velocity Report</h3>

          <p>
            Stock by item velocity
          </p>

        </div>

      </div>

      <table>

        <tbody>

          <tr>

            <td>FAST</td>

            <td>
              ${v.fastQuantity ?? 0}
              units
            </td>

            <td>
              ${v.fastItems ?? 0}
              assignments
            </td>

          </tr>

          <tr>

            <td>MEDIUM</td>

            <td>
              ${v.mediumQuantity ?? 0}
              units
            </td>

            <td>
              ${v.mediumItems ?? 0}
              assignments
            </td>

          </tr>

          <tr>

            <td>SLOW</td>

            <td>
              ${v.slowQuantity ?? 0}
              units
            </td>

            <td>
              ${v.slowItems ?? 0}
              assignments
            </td>

          </tr>

          <tr>

            <th>Total</th>

            <th>
              ${
                (v.fastQuantity || 0) +
                (v.mediumQuantity || 0) +
                (v.slowQuantity || 0)
              }
              units
            </th>

            <th>
              ${v.totalAssignments ?? 0}
              assignments
            </th>

          </tr>

        </tbody>

      </table>

    `;

  } catch (e) {

    document.getElementById(
      "utilReport"
    ).innerHTML =
      errorHtml(e.message);

    document.getElementById(
      "velocityReport"
    ).innerHTML = "";

  }
}