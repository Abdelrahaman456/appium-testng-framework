import openpyxl

# Create a new workbook and select active sheet
wb = openpyxl.Workbook()
ws = wb.active
ws.title = "Test Flows Matrix"

# Data from the user's table
data = [
    ["Product", "Flow", "Insurance Type / Trip", "Card / Traveler Type", "Status", "Steps Outline"],
    ["Motor", "Flow 1", "New Insurance", "Sequence Number", "✅ Finished", "Enter details -> OTP -> Cover Type -> Checkout"],
    ["Motor", "Flow 2", "New Insurance", "Custom Card", "✅ Finished", "Enter details -> OTP -> Vehicle Details -> Checkout"],
    ["Motor", "Flow 3", "Ownership Transfer", "Sequence Number", "✅ Finished", "Enter details -> OTP -> Cover Type -> Checkout"],
    ["Motor", "Flow 4", "Ownership Transfer", "Custom Card", "✅ Finished", "Enter details -> OTP -> Cover Type -> Checkout"],
    ["Travel", "Flow 1", "Single Trip", "Individual", "⏳ In Progress", "Select Single Trip -> Dates -> Phone -> Individual"],
    ["Travel", "Flow 1", "Single Trip", "With Family", "📅 Later", "Select Single Trip -> Dates -> Phone -> Family"],
    ["Travel", "Flow 1", "Single Trip", "Group", "📅 Later", "Select Single Trip -> Dates -> Phone -> Group"],
    ["Travel", "Flow 2", "Multi Trip (1 year)", "Individual", "📅 Later", "Select Multi Trip -> Date (No Return) -> Phone -> Individual"],
    ["Travel", "Flow 2", "Multi Trip (1 year)", "With Family", "📅 Later", "Select Multi Trip -> Date (No Return) -> Phone -> Family"],
    ["Travel", "Flow 2", "Multi Trip (1 year)", "Group", "📅 Later", "Select Multi Trip -> Date (No Return) -> Phone -> Group"]
]

# Write data to worksheet
for row in data:
    ws.append(row)

# Adjust column widths for better readability
column_widths = {
    'A': 10,  # Product
    'B': 10,  # Flow
    'C': 25,  # Insurance Type / Trip
    'D': 25,  # Card / Traveler Type
    'E': 15,  # Status
    'F': 80   # Steps Outline
}

for col, width in column_widths.items():
    ws.column_dimensions[col].width = width

# Save the workbook
output_path = "Test_Flows_Matrix.xlsx"
wb.save(output_path)
print(f"Successfully created {output_path}")
