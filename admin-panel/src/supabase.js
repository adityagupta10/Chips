import { createClient } from '@supabase/supabase-js'

const SUPABASE_URL = 'https://acswwfbdbeqxbmadszue.supabase.co'
const SUPABASE_ANON_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImFjc3d3ZmJkYmVxeGJtYWRzenVlIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY2NjQ0NzcsImV4cCI6MjA5MjI0MDQ3N30.0wLR8V0WC00cC5pR5J-qmYCbk2kKZ3kyeQ-EpcIkM7s'

export const supabase = createClient(SUPABASE_URL, SUPABASE_ANON_KEY)
